---
description: Run the full pre-commit quality checklist against staged changes
agent: pre-commit-check
subtask: true
---

Run the pre-commit quality checklist against all currently staged changes.

First check what's staged:
!`git diff --cached --stat`

Then analyze each changed file against the engineering standards checklist defined in `engineering-standards/docs/PRE_COMMIT_CHECKLIST.md`.

Report pass/fail for each checklist item and provide a final recommendation on whether it's safe to commit.
