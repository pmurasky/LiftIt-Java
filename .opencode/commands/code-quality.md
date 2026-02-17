---
description: Quick code quality scan against engineering standards
agent: standards-review
subtask: true
---

Perform a quick code quality scan on the specified target.

Target: $ARGUMENTS

Run a fast scan checking these quality gates:

### Code Metrics
- [ ] Methods <= 15 lines (excluding blanks and braces)
- [ ] Classes <= 300 lines
- [ ] Private methods: 0-2 per class (SRP indicator)
- [ ] Parameters <= 5 per method
- [ ] No duplicated code blocks

### Naming and Readability
- [ ] Meaningful variable/method/class names
- [ ] No single-letter variables (except loop indices)
- [ ] No abbreviations or acronyms without context
- [ ] Class names don't contain "Manager", "Handler", "Utility" (SRP smell)

### Common Anti-Patterns
- [ ] No God classes (doing too many things)
- [ ] No Feature Envy (method using another class's data extensively)
- [ ] No Primitive Obsession (should be value objects)
- [ ] No hardcoded values (use constants or config)
- [ ] No commented-out code
- [ ] No TODO/FIXME without issue references
- [ ] No hardcoded secrets or credentials

### Quick SOLID Check
- [ ] SRP: Each class has one reason to change
- [ ] OCP: No switch/if-else chains on types
- [ ] DIP: Dependencies injected, not instantiated directly

Output a concise report:

```
## Code Quality Scan

### Target: <file or directory>
### Score: X/10

### Issues Found
| Severity | File:Line | Issue | Fix |
|----------|-----------|-------|-----|
| Critical | ... | ... | ... |
| Warning  | ... | ... | ... |

### Clean Code Highlights
- What the code does well
```

Reference `engineering-standards/docs/CODING_STANDARDS.md` and `engineering-standards/docs/PRE_COMMIT_CHECKLIST.md` for detailed standards.
