---
description: TDD micro-commit workflow enforcement for all code changes
---

# Micro-Commit Workflow

For ALL code changes, follow the STOP -> RED -> GREEN -> COMMIT -> REFACTOR -> COMMIT cycle.

Key rules:
- One logical change per commit (never bundle multiple changes)
- Every commit MUST be production-ready (tests pass, builds, no lint errors)
- Conventional Commits: `<type>(<scope>): <description>`

For the full workflow, read `engineering-standards/docs/AI_AGENT_WORKFLOW.md`.
