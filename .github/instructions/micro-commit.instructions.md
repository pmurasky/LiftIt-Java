---
applyTo: "**/*.{kt,java,ts,js,py,go,rs,cs,cpp,c,swift,rb}"
---

# Micro-Commit Workflow

When making code changes, follow the TDD micro-commit workflow strictly.

## Every Logical Change = One Commit

Never bundle multiple logical changes into one commit:
- One refactoring step per commit
- One feature implementation per commit
- One test update per commit
- One documentation update per commit

## TDD Cycle
For ALL code changes, follow the STOP -> RED -> GREEN -> COMMIT -> REFACTOR -> COMMIT cycle. See `engineering-standards/docs/AI_AGENT_WORKFLOW.md` for the full workflow.

## Commit Message Format

```
<type>(<scope>): <description>

[body explaining WHY, not WHAT]
```

Types: `feat`, `fix`, `refactor`, `test`, `docs`, `perf`, `chore`

## Production-Ready Commits

Every commit MUST be production-ready:
- All tests pass
- Build succeeds
- No lint errors
- Code is deployable

## Red Flags - STOP and Ask

- Change requires modifying 10+ files
- Change will break a public API
- Tests failing after your change
- Code being changed has < 80% test coverage

For the full workflow, see `engineering-standards/docs/AI_AGENT_WORKFLOW.md`.
