---
description: Code review checklist for SOLID principles, code quality, and testing standards
globs: "**/*.{kt,java,ts,js,py,go,rs,cs,cpp,c,swift,rb}"
---

# Code Review Standards

When reviewing or writing code, enforce these standards:

## SOLID Principles
- **SRP**: Each class has ONE reason to change. Flag classes > 300 lines, > 10 methods, or named "Manager/Handler/Utility"
- **OCP**: No switch/when on type checks, no hard-coded class instantiation. Use Strategy Pattern.
- **LSP**: No UnsupportedOperationException in subclasses, no type checking before casting
- **ISP**: No interfaces with > 5 methods, no empty interface method implementations
- **DIP**: No direct instantiation of dependencies. Use constructor injection.

## Code Quality Gates
- Methods: 15 lines max (excluding blanks and braces)
- Classes: 300 lines max
- Private methods: 0-2 per class (SRP guideline)
- Parameters: 5 max per method (use parameter objects)
- No duplicated code (DRY)

## Testing
- 80% minimum test coverage, 100% for critical paths
- Given-When-Then structure
- Descriptive test names (e.g., `shouldSelectLatestVersionWhenAvailable`)

For detailed examples, read `engineering-standards/docs/CODING_STANDARDS.md`, `engineering-standards/docs/PRE_COMMIT_CHECKLIST.md`, `engineering-standards/docs/DESIGN_PATTERNS.md`, and `engineering-standards/docs/SOLID_PRINCIPLES.md`.
