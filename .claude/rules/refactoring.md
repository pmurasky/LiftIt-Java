---
description: Refactoring guidelines and micro-commit workflow for safe refactoring
globs: "**/*.{kt,java,ts,js,py,go,rs,cs,cpp,c,swift,rb}"
---

# Refactoring Standards

When refactoring code, follow these rules strictly:

## Prerequisites (Before Any Refactoring)
1. **Tests MUST exist** before refactoring begins
   - If coverage < 80%, write tests FIRST (separate commits)
   - All tests must be green before starting
2. **Understand the scope** - know what you're changing and why

## Micro-Commit Refactoring
Each refactoring step is its own commit:
- Extract method -> COMMIT
- Rename variable -> COMMIT
- Move class -> COMMIT
- Introduce interface -> COMMIT

Never bundle multiple refactoring steps into one commit.

## Safe Refactoring Patterns
- **Extract Method**: Method > 15 lines or has a comment explaining a block
- **Extract Class**: Class > 300 lines or has > 2 private methods (SRP violation)
- **Introduce Parameter Object**: Method has > 5 parameters
- **Replace Conditional with Strategy**: switch/if-else chain on types (OCP violation)
- **Extract Interface**: Direct dependency on concrete class (DIP violation)
- **Inline Variable**: Variable used only once and adds no clarity

## After Each Refactoring Step
1. Run ALL tests (not just the ones you think are affected)
2. Verify build succeeds
3. Verify no lint errors
4. Commit with: `refactor(<scope>): <what was improved>`

## When to Refactor
- After making a test pass (GREEN -> REFACTOR cycle)
- Before adding a new feature (to make the change easier)
- When you encounter code smells during a task

## When NOT to Refactor
- Without tests in place
- As part of a feature commit (separate commits)
- Large-scale refactoring without user approval (10+ files = red flag)

For the full refactoring workflow, read `engineering-standards/docs/AI_AGENT_WORKFLOW.md`.
