---
applyTo: "**/*.{kt,java,ts,js,py,go,rs,cs,cpp,c,swift,rb}"
---

# Code Review Standards

When reviewing or generating code, enforce these quality gates:

## SOLID Violations to Flag
- **SRP**: Classes > 300 lines, > 2 private methods, "Manager/Handler/Utility" names
- **OCP**: switch/when on types, hard-coded instantiation
- **LSP**: UnsupportedOperationException in subclasses, type checks before casting
- **ISP**: Interfaces > 5 methods, empty implementations
- **DIP**: Direct dependency instantiation without constructor injection

## Quality Metrics
- Methods: 15 lines max
- Classes: 300 lines max
- Parameters: 5 max per method
- No duplicated code
- No TODO/FIXME without ticket reference
- No commented-out code

## Testing Requirements
- 80% minimum coverage, 100% for critical paths
- Given-When-Then test structure
- Descriptive test names

See `engineering-standards/docs/PRE_COMMIT_CHECKLIST.md`, `engineering-standards/docs/CODING_STANDARDS.md`, `engineering-standards/docs/DESIGN_PATTERNS.md`, and `engineering-standards/docs/SOLID_PRINCIPLES.md` for detailed examples.
