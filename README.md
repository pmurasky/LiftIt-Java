# LiftIt Java Starter

Minimal Gradle starter project for Java 25.

## Project structure

- `src/main/java/com/liftit/App.java` - application entrypoint
- `src/test/java/com/liftit/AppTest.java` - JUnit 5 sample test
- `build.gradle` - Gradle build configuration
- `settings.gradle` - Gradle project settings
- `gradle/wrapper/gradle-wrapper.properties` - wrapper pinned to Gradle 9.3.1
- `gradlew` / `gradlew.bat` - wrapper launchers

## Requirements

- Java 25+
- PostgreSQL 17+ (or Docker for local development)
- No global Gradle install required (uses wrapper)

## Build and test

```bash
# Unit tests only (fast, no database required)
./gradlew test

# Unit + integration tests (requires Docker for Testcontainers)
./gradlew integrationTest
```

## Run

```bash
./gradlew bootRun
```

## Database migrations

Database schema is managed by **Liquibase** using SQL-format changelogs.

- Master changelog: `src/main/resources/db/changelog/db.changelog-master.xml`
- Changeset files: `src/main/resources/db/changelog/V{n}__{description}.sql`
- Migrations run automatically on application startup

Each changeset file must begin with:
```sql
--liquibase formatted sql

--changeset liftit:<slug>
-- SQL here
--rollback -- inverse SQL here
```

When adding a new migration:
1. Create `src/main/resources/db/changelog/V{n+1}__{description}.sql`
2. Add a `<include file="..."/>` entry to `db.changelog-master.xml`

See `docs/architecture-overview.md` and `docs/adr/0001-use-liquibase-for-database-migrations.md` for full conventions.

## Code quality — OpenRewrite

Automated refactoring is managed by the [OpenRewrite Gradle plugin](https://docs.openrewrite.org/).
OpenRewrite is **not** run on every build; it is run on demand before committing quality improvements.

```bash
# Preview what active recipes would change (no files modified)
./gradlew rewriteDryRun
# Patch written to: build/reports/rewrite/rewrite.patch

# Apply changes after reviewing the dry-run diff
./gradlew rewriteRun

# Explore all available recipes
./gradlew rewriteDiscover
```

Active recipes are configured in the `rewrite { }` block in `build.gradle`.
Always run `rewriteDryRun` first, review the patch, then run `rewriteRun`.

See `docs/adr/0002-use-openrewrite-for-automated-refactoring.md` for the full recipe
selection policy, tier guidance, and one-time migration workflow.
