---
description: Testing standards enforcement when editing test files
globs: "**/*{Test,test,Spec,spec,_test}*.{kt,java,ts,js,py,go,rs,cs,cpp,c,swift,rb}"
---

# Testing Standards

When writing or editing tests, enforce these standards:

## Test Structure
- Use **Given-When-Then** format (Arrange-Act-Assert)
- One logical assertion per test (multiple asserts allowed if testing one behavior)
- Tests must be independent (no shared mutable state between tests)

## Test Naming
- Descriptive names that explain the scenario: `shouldReturnEmptyListWhenNoResultsFound`
- Never use generic names: `test1`, `testMethod`, `itWorks`
- Name format: `should<Expected>When<Condition>`

## Coverage Requirements
- 80% minimum line coverage for standard code
- 100% coverage for critical paths (auth, payments, data integrity)
- Cover happy path, edge cases, and error paths

## Test Quality
- Test behavior, not implementation details
- No logic in tests (no if/else, loops, or try/catch)
- Use test fixtures and builders for complex test data
- Mock external dependencies, not internal collaborators
- Prefer fakes over mocks when possible

## What to Test
- Public API methods (every public method should have tests)
- Edge cases: null, empty, boundary values, overflow
- Error paths: exceptions, invalid input, timeouts
- State transitions and side effects

## What NOT to Do
- Never commit failing tests (every commit must be production-ready, no exceptions)
- Never skip tests with `@Disabled`/`@Ignore` without an issue reference
- Never test private methods directly (test through public API)
- Never use `Thread.sleep()` in tests (use async utilities)

For detailed testing guidance, read `engineering-standards/docs/CODING_PRACTICES.md`.
