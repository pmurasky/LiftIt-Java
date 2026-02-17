---
description: Code review checklist for SOLID violations and quality metrics
globs: "**/*.{kt,java,ts,js,py,go,rs,cs,cpp,c,swift,rb}"
---

# Code Review Checklist

When reviewing code, check for:

## SOLID Violations
- **SRP**: Classes > 300 lines, > 2 private methods, "Manager/Handler/Utility" names
- **OCP**: switch/when on types, hard-coded instantiation, if-else chains on object types
- **LSP**: UnsupportedOperationException in subclasses, type checks before casting
- **ISP**: Interfaces > 5 methods, empty interface implementations
- **DIP**: Direct dependency instantiation, concrete class imports instead of interfaces

## Quality Metrics
- Methods > 15 lines (excluding blanks/braces)
- Classes > 300 lines
- Methods with > 5 parameters
- Duplicated code blocks
- TODO/FIXME without ticket reference
- Commented-out code
- Hardcoded secrets

## Testing
- 80% minimum coverage, 100% for critical paths
- Given-When-Then test structure
- Descriptive test names

See `engineering-standards/docs/PRE_COMMIT_CHECKLIST.md`, `engineering-standards/docs/DESIGN_PATTERNS.md`, and `engineering-standards/docs/SOLID_PRINCIPLES.md` for the full checklist and guidance.
