# Engineering Standards Agent

## Overview

This project follows reusable engineering standards for AI coding agents (OpenCode, Cursor, Copilot, etc.). It enforces coding practices, SOLID principles, TDD micro-commit workflows, and code quality standards.

## Core Principles

You MUST follow these principles for ALL code changes. No exceptions.

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

### 8. Dependency Versioning
- **Always use the latest stable (GA) version** when adding any dependency — never intentionally use an older version
- **Verify no known CVEs** before adding: check [NVD](https://nvd.nist.gov/), [OSV](https://osv.dev/), or [GitHub Advisory Database](https://github.com/advisories)
- **BOM-managed projects** (Spring Boot, etc.): trust the BOM for transitive deps — do not override BOM-managed versions manually
- **Update on disclosure**: when a CVE is disclosed for a dependency, update immediately — do not wait for a scheduled review

## External File Loading

CRITICAL: When you encounter a file reference below, use your Read tool to load it on a need-to-know basis. They contain detailed instructions relevant to SPECIFIC tasks.

Do NOT preemptively load all references. Use lazy loading based on actual need.

## Detailed Standards References

For the micro-commit workflow and AI agent instructions: @engineering-standards/docs/AI_AGENT_WORKFLOW.md
For language-agnostic coding practices, SOLID examples, testing, and TDD: @engineering-standards/docs/CODING_PRACTICES.md
For the standards index (table of contents): @engineering-standards/docs/CODING_STANDARDS.md
For Java-specific conventions (when working with Java): @engineering-standards/docs/JAVA_STANDARDS.md
For Kotlin-specific conventions (when working with Kotlin): @engineering-standards/docs/KOTLIN_STANDARDS.md
For the pre-commit quality checklist: @engineering-standards/docs/PRE_COMMIT_CHECKLIST.md
For design patterns guidance: @engineering-standards/docs/DESIGN_PATTERNS.md
For SOLID principles with multi-language examples: @engineering-standards/docs/SOLID_PRINCIPLES.md

## Before Making ANY Code Changes

1. Read and acknowledge: "I will follow the micro-commit workflow"
2. Create a task list using TodoWrite to break down the work
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

## Agent Pledge

"I will follow the engineering standards for every code change, no exceptions. Every commit will be production-ready."
