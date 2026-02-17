# Engineering Standards

Follow these engineering standards for ALL code changes in this repository.

## TDD Micro-Commit Cycle
For ALL code changes, follow the STOP -> RED -> GREEN -> COMMIT -> REFACTOR -> COMMIT cycle. See `engineering-standards/docs/AI_AGENT_WORKFLOW.md` for the full workflow.

## Code Quality Rules
- Maximum method length: 15 lines (excluding blank lines and braces)
- Maximum class length: 300 lines (consider refactoring if larger)
- Maximum 0-2 private methods per class (SRP guideline)
- Maximum 5 parameters per method (use parameter objects)
- No duplicated code (DRY principle)

## SOLID Principles
- **SRP**: Each class has ONE reason to change
- **OCP**: Open for extension, closed for modification (use Strategy Pattern)
- **LSP**: Subtypes must be substitutable for base types
- **ISP**: Prefer focused interfaces over fat interfaces
- **DIP**: Depend on abstractions, not concrete classes (use dependency injection)

## Commit Standards
- One logical change per commit
- Conventional Commits: `<type>(<scope>): <description>`
- Types: feat, fix, refactor, test, docs, perf, chore
- Every commit must be production-ready

## Testing Standards
- 80% minimum test coverage, 100% for critical paths
- Given-When-Then structure
- Descriptive test names (e.g., `shouldSelectLatestVersionWhenAvailable`)

## Detailed References
See `engineering-standards/docs/` directory for comprehensive standards:
- `engineering-standards/docs/AI_AGENT_WORKFLOW.md` - Micro-commit workflow
- `engineering-standards/docs/CODING_PRACTICES.md` - Language-agnostic practices, SOLID examples, and TDD
- `engineering-standards/docs/CODING_STANDARDS.md` - Standards index (table of contents)
- `engineering-standards/docs/PRE_COMMIT_CHECKLIST.md` - Pre-commit checklist
- `engineering-standards/docs/DESIGN_PATTERNS.md` - Design patterns catalog and guidance
- `engineering-standards/docs/SOLID_PRINCIPLES.md` - SOLID principles with multi-language examples
- `engineering-standards/docs/JAVA_STANDARDS.md` - Java conventions (when working with Java)
- `engineering-standards/docs/KOTLIN_STANDARDS.md` - Kotlin conventions (when working with Kotlin)
