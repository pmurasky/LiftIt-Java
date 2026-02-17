# Engineering Standards

You MUST follow these engineering standards for ALL code changes. No exceptions.

## Core Principles

### 1. Micro-Commit Workflow
Every logical change = one commit. Never bundle multiple logical changes into one commit.

- One refactoring step per commit
- One feature implementation per commit
- One test update per commit
- One documentation update per commit

### 2. TDD Micro-Commit Cycle
For ALL code changes, follow the STOP -> RED -> GREEN -> COMMIT -> REFACTOR -> COMMIT cycle. See `engineering-standards/docs/AI_AGENT_WORKFLOW.md` for the full workflow.

### 3. Production-Ready Commits
Every commit MUST be production-ready:
- All tests pass
- Build succeeds
- No lint errors
- Code is deployable

### 4. Code Quality Rules
- Maximum method length: 15 lines (excluding blank lines and braces)
- Maximum class length: 300 lines (consider refactoring if larger)
- Maximum 0-2 private methods per class (SRP guideline)
- Maximum 5 parameters per method (use parameter objects)
- No duplicated code (DRY principle)

### 5. SOLID Principles
- **SRP**: Each class has ONE reason to change
- **OCP**: Open for extension, closed for modification (use Strategy Pattern)
- **LSP**: Subtypes must be substitutable for base types
- **ISP**: Prefer focused interfaces over fat interfaces
- **DIP**: Depend on abstractions, not concrete classes (use dependency injection)

### 6. Commit Message Format
Use Conventional Commits:
```
<type>(<scope>): <description>

[body explaining WHY, not WHAT]
```

Types: feat, fix, refactor, test, docs, perf, chore

### 7. Testing Standards
- Minimum 80% test coverage overall
- 100% coverage for critical paths
- Use Given-When-Then structure
- Descriptive test names (e.g., `shouldSelectLatestVersionWhenAvailable`)
- Never commit failing tests (every commit must be production-ready, no exceptions)

## Detailed Standards References

When you need detailed guidance, read the relevant file:

- `engineering-standards/docs/AI_AGENT_WORKFLOW.md` - Micro-commit workflow and AI agent instructions
- `engineering-standards/docs/CODING_PRACTICES.md` - Language-agnostic coding practices, SOLID examples, testing, and TDD
- `engineering-standards/docs/CODING_STANDARDS.md` - Standards index (table of contents)
- `engineering-standards/docs/JAVA_STANDARDS.md` - Java-specific conventions (when working with Java)
- `engineering-standards/docs/KOTLIN_STANDARDS.md` - Kotlin-specific conventions (when working with Kotlin)
- `engineering-standards/docs/PRE_COMMIT_CHECKLIST.md` - Pre-commit quality checklist
- `engineering-standards/docs/DESIGN_PATTERNS.md` - Design patterns catalog and guidance
- `engineering-standards/docs/SOLID_PRINCIPLES.md` - SOLID principles with multi-language examples

Load these on a need-to-know basis, not all at once.

## Before Making ANY Code Changes

1. Read and acknowledge: "I will follow the micro-commit workflow"
2. Break work into a task list of micro-commits
3. Execute one task at a time, committing after each one
4. Run tests before every commit
5. Follow the pre-commit checklist

## Red Flags (Stop and Ask User)

If you encounter these situations, STOP and ask:

1. **Unclear scope**: Change requires modifying 10+ files
2. **Breaking change**: Change will break a public API
3. **Test failures**: Tests failing after your change
4. **Conflicting patterns**: Existing code doesn't follow SOLID
5. **Missing tests**: Code being changed has < 80% coverage
