---
description: Core engineering standards - TDD, micro-commits, SOLID, code quality
alwaysApply: true
---

# Engineering Standards

You MUST follow these standards for ALL code changes.

## TDD Micro-Commit Cycle
For ALL code changes, follow the STOP -> RED -> GREEN -> COMMIT -> REFACTOR -> COMMIT cycle. See `engineering-standards/docs/AI_AGENT_WORKFLOW.md` for the full workflow.

## Code Quality Gates
- Methods: 15 lines max (excluding blanks and braces)
- Classes: 300 lines max
- Private methods: 0-2 per class
- Parameters: 5 max per method (use parameter objects)
- No duplicated code (DRY)

## SOLID Principles
- **SRP**: Each class has ONE reason to change
- **OCP**: Open for extension, closed for modification (Strategy Pattern)
- **LSP**: Subtypes substitutable for base types
- **ISP**: Focused interfaces (< 5 methods)
- **DIP**: Depend on abstractions, use constructor injection

## Commits
- One logical change per commit
- Conventional Commits: `<type>(<scope>): <description>`
- Every commit production-ready (tests pass, builds, no lint errors)

## Red Flags - STOP and Ask
- Modifying 10+ files
- Breaking a public API
- Tests failing after your change
- Code has < 80% test coverage

## Detailed References
Read these files for detailed guidance when needed:
- `engineering-standards/docs/AI_AGENT_WORKFLOW.md` - Micro-commit workflow
- `engineering-standards/docs/CODING_PRACTICES.md` - Language-agnostic practices, SOLID examples, and TDD
- `engineering-standards/docs/CODING_STANDARDS.md` - Standards index (table of contents)
- `engineering-standards/docs/PRE_COMMIT_CHECKLIST.md` - Pre-commit checklist
- `engineering-standards/docs/DESIGN_PATTERNS.md` - Design patterns catalog and guidance
- `engineering-standards/docs/SOLID_PRINCIPLES.md` - SOLID principles with multi-language examples
- `engineering-standards/docs/JAVA_STANDARDS.md` - Java conventions (when working with Java)
- `engineering-standards/docs/KOTLIN_STANDARDS.md` - Kotlin conventions (when working with Kotlin)
