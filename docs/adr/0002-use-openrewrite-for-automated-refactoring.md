# ADR-0002: Use OpenRewrite for Automated Code Refactoring

## Status

Accepted

## Date

2026-02-24

## Context

As the LiftIt codebase grows, keeping code quality consistent and dependencies up-to-date
becomes increasingly expensive when done manually. Common problems that emerge over time:

- Dependency version drift and security vulnerabilities
- Inconsistent code style across contributors
- Manual application of recurring best practices (method references, pattern matching,
  modifier ordering, etc.)
- Large-scale migrations (e.g., Spring Boot major version upgrades) requiring coordinated
  changes across hundreds of files

[OpenRewrite](https://docs.openrewrite.org/) is a recipe-based automated refactoring engine
that analyses and transforms Java source code safely and repeatably via AST (Abstract Syntax
Tree) rewrites — not text substitution. It integrates with Gradle as a first-class plugin and
is maintained by Moderne with broad adoption in the Java ecosystem.

## Decision

Integrate the **OpenRewrite Gradle plugin** (`org.openrewrite.rewrite`) as a permanent part
of the LiftIt build. Run curated recipes on a **periodic basis** (not on every build) to
apply automated improvements and keep the codebase clean.

### Plugin configuration (in `build.gradle`)

```groovy
plugins {
    id 'org.openrewrite.rewrite' version '<latest-stable>'
}

dependencies {
    // Use the BOM — do NOT pin individual recipe module versions manually
    rewrite platform('org.openrewrite.recipe:rewrite-recipe-bom:latest.release')
    rewrite 'org.openrewrite.recipe:rewrite-spring'
    rewrite 'org.openrewrite.recipe:rewrite-java-dependencies'
    rewrite 'org.openrewrite.recipe:rewrite-static-analysis'
}

rewrite {
    activeRecipe(
        // List active recipes here — see "Recipe Selection Policy" below
    )
}
```

> **Dependency configuration name**: use `rewrite` (not `rewriteRecipes` — that name does
> not exist in the official plugin API).

### Gradle tasks

| Task | Effect | When to use |
|------|--------|-------------|
| `./gradlew rewriteDiscover` | Lists all recipes available on the classpath | Exploring what's possible |
| `./gradlew rewriteDryRun` | Previews changes; writes a patch to `build/reports/rewrite/rewrite.patch` — no files modified | Before every `rewriteRun` |
| `./gradlew rewriteRun` | Applies active recipes, modifying source files in place | After reviewing the dry-run diff |

**Always run `rewriteDryRun` first and review the patch before running `rewriteRun`.**

## Recipe Selection Policy

Not all recipes are appropriate to activate permanently. Recipes are categorised as follows:

### Tier 1 — Always Active (safe, high signal, low noise)

These are safe to leave active at all times. They produce changes that are always correct
and never require judgement.

| Recipe | Purpose |
|--------|---------|
| `org.openrewrite.staticanalysis.CommonStaticAnalysis` | Broad sweep: method references, final classes, boolean simplification, and ~50 other checks |
| `org.openrewrite.staticanalysis.CodeCleanup` | Removes unused imports, redundant casts, unnecessary parentheses, empty blocks |
| `org.openrewrite.staticanalysis.MissingOverrideAnnotation` | Adds `@Override` where missing |
| `org.openrewrite.staticanalysis.InstanceOfPatternMatch` | Replaces old-style instanceof+cast with Java 16+ pattern variables (we run Java 25) |
| `org.openrewrite.staticanalysis.FinalizePrivateFields` | Marks private fields `final` when they are only assigned in the constructor |
| `org.openrewrite.staticanalysis.JavaApiBestPractices` | Catches common Java API misuse patterns |

### Tier 2 — Run Periodically (review output before committing)

These are correct but may produce noisy or stylistic changes that warrant a human review
before committing.

| Recipe | Purpose | Notes |
|--------|---------|-------|
| `org.openrewrite.java.dependencies.DependencyInsight` | Audit all resolved dependency versions | Read-only report; run before updating deps |
| `org.openrewrite.staticanalysis.NeedBraces` | Enforce braces on all control flow statements | Team style choice |
| `org.openrewrite.staticanalysis.ReplaceDuplicateStringLiterals` | Extracts repeated string literals to constants | Review constant names |
| `org.openrewrite.java.logging.ChangeLoggersToPrivate` | Ensures logger fields are `private static final` | Run when loggers are added |

### Tier 3 — Migration Recipes (run once, on demand)

These are used for one-time major migrations. Activate them, run, review, commit, then
**remove them from `activeRecipe`**.

| Recipe | Purpose | Trigger |
|--------|---------|---------|
| `org.openrewrite.java.spring.boot3.UpgradeSpringBoot_3_x` | Migrate to Spring Boot 3.x | N/A — already on Boot 4 |
| Future Boot 4→5 recipe | Migrate to Spring Boot 5.x when available | When Boot 5 is released |
| `org.openrewrite.java.migrate.UpgradeToJava21` / `UpgradeToJava25` | Java version migrations | When upgrading Java version |

### Recipes to Avoid for This Project

| Recipe | Reason to skip |
|--------|---------------|
| `FinalizeLocalVariables` / `FinalizeMethodArguments` | Adds `final` everywhere — very noisy, low value for this codebase |
| `ExplicitInitialization` | Removes redundant `= null` etc. — cosmetic noise |
| `spring.boot2.*` / `spring.boot3.*` | Already on Spring Boot 4; these would be no-ops or harmful |
| `java.logging.*` (most) | No loggers exist yet; revisit when SLF4J is added |

## Workflow: How to Run OpenRewrite

### Routine maintenance (Tier 1 recipes)

```bash
# 1. Preview what will change
./gradlew rewriteDryRun
# Review: build/reports/rewrite/rewrite.patch

# 2. If the diff looks correct, apply
./gradlew rewriteRun

# 3. Run tests to verify nothing broke
./gradlew test

# 4. Commit with a refactor commit
git add -A
git commit -m "refactor: apply OpenRewrite CommonStaticAnalysis / CodeCleanup recipes"
```

### One-time migration (Tier 3 recipe)

```bash
# 1. Add the migration recipe to activeRecipe() in build.gradle
# 2. Preview
./gradlew rewriteDryRun

# 3. Apply
./gradlew rewriteRun

# 4. Run full test suite (unit + integration)
./gradlew test integrationTest

# 5. Commit
git commit -m "refactor: migrate to Spring Boot X.Y via OpenRewrite"

# 6. REMOVE the migration recipe from activeRecipe() — it is now done
git commit -m "chore(build): remove one-time Spring Boot migration recipe"
```

### Exploring available recipes

```bash
# List all recipes available on the classpath (2949+ when all recipe modules are present)
./gradlew rewriteDiscover

# Filter in the terminal
./gradlew rewriteDiscover 2>&1 | grep -i "staticanalysis"
./gradlew rewriteDiscover 2>&1 | grep -i "spring.boot"
```

## Alternatives Considered

### Manual code review only

- ✅ No additional tooling
- ❌ Inconsistent enforcement — relies entirely on reviewer attention
- ❌ Large-scale migrations (e.g., Boot upgrades) require days of manual effort
- ❌ Does not scale as the team grows

### Checkstyle / PMD only

- ✅ Widely used, IDE-integrated
- ❌ These tools only *detect* violations — they do not fix them
- ❌ OpenRewrite can *apply* fixes in addition to detecting them; the two tools are
  complementary, not alternatives

### Moderne Platform (commercial)

- ✅ Cross-repository recipe execution at scale
- ❌ Overkill for a single-repository project at this stage
- Revisit when the project grows to multiple services

## Consequences

### Positive

- Automated, safe application of Java best practices across the codebase
- Spring Boot and Java major-version migrations become a Gradle task rather than a
  multi-day manual effort
- Consistent code style enforced by tooling, not code review
- `rewriteDryRun` provides a free static analysis report on demand

### Negative

- Recipe output must always be reviewed before committing — `rewriteRun` modifies files
  in place and the diff must be verified
- Plugin version must be kept up-to-date manually (not BOM-managed)
- Some recipes are opinionated; wrong recipe selection can produce noisy or unwanted changes

### Neutral

- OpenRewrite does not run on every build (no performance impact on CI)
- The `rewrite` dependency configuration is only resolved when a `rewrite*` task is executed

## References

- [OpenRewrite Quickstart](https://docs.openrewrite.org/running-recipes/getting-started)
- [Gradle Plugin Configuration](https://docs.openrewrite.org/reference/gradle-plugin-configuration)
- [Recipe Catalog](https://docs.openrewrite.org/recipes)
- [rewrite-static-analysis recipes](https://docs.openrewrite.org/recipes/staticanalysis)
- GitHub issue [#54](https://github.com/pmurasky/LiftIt-Java/issues/54) — initial integration
