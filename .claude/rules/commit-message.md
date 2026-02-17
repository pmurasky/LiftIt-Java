---
description: Commit message format and micro-commit standards
---

# Commit Message Standards

## Format (Conventional Commits)

Every commit message MUST follow this format:

```
<type>(<scope>): <description>

[optional body explaining WHY, not WHAT]
```

## Types
- `feat` - New feature or wholly new functionality
- `fix` - Bug fix
- `refactor` - Code restructuring without behavior change
- `test` - Adding or updating tests only
- `docs` - Documentation changes only
- `perf` - Performance improvement
- `chore` - Build, CI, tooling, or dependency changes

## Rules
- **Description**: Lowercase, imperative mood, no period at end
- **Scope**: Module or component affected (e.g., `auth`, `api`, `parser`)
- **Body**: Explain WHY the change was made, not WHAT changed (the diff shows that)
- **Length**: Subject line <= 72 characters

## Good Examples
```
feat(auth): add OAuth2 token refresh flow
fix(parser): handle null input in date parser
refactor(api): extract validation into separate class
test(user): add edge case tests for email validation
```

## Bad Examples (Never Do These)
```
fix: bug fix                    # Too vague
update code                     # No type, no scope, no description
feat: add stuff and fix things  # Multiple changes in one commit
WIP                             # Never commit work in progress
refactor(auth): Refactor auth.  # Don't capitalize, don't add period
```

## Micro-Commit Rule
- One logical change per commit
- Never bundle feat + refactor + test in one commit
- Commit order: refactor -> feat -> test -> docs

For detailed commit workflow, read `engineering-standards/docs/AI_AGENT_WORKFLOW.md`.
