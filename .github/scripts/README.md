# GitHub Issues Setup Scripts

This directory contains scripts to help set up GitHub Issues for the LiftIt-Java project.

## Prerequisites

- **GitHub CLI (`gh`)**: Install with `sudo apt install gh` or visit [cli.github.com](https://cli.github.com/)
- **Authentication**: Run `gh auth login` to authenticate with GitHub

## Scripts

### 1. `setup-labels.sh`

Creates all project labels from `.github/labels.yml` using `github-label-sync`.

**Usage:**
```bash
./.github/scripts/setup-labels.sh
```

This will create/update labels in the following categories:
- **Type**: bug, enhancement, documentation, refactor, test, chore
- **Priority**: critical, high, medium, low
- **Status**: needs-triage, blocked, in-progress, ready-for-review
- **Area**: build, ci-cd, testing, engineering-standards, dependencies
- **Effort**: small, medium, large
- **Special**: good-first-issue, help-wanted, question

### 2. `create-initial-issues.sh`

Creates 13 initial issues for the project covering:
- Documentation improvements (3 issues)
- CI/CD enhancements (4 issues)
- Code quality tooling (3 issues)
- Feature development (2 issues)
- Testing improvements (1 issue)

**Usage:**
```bash
./.github/scripts/create-initial-issues.sh
```

## Manual Setup (Without GitHub CLI)

If you prefer not to use the GitHub CLI, you can:

1. **Labels**: Navigate to `https://github.com/pmurasky/LiftIt-Java/labels` and manually create labels from `.github/labels.yml`

2. **Issues**: Use the issue templates when creating new issues:
   - Go to `https://github.com/pmurasky/LiftIt-Java/issues/new/choose`
   - Select the appropriate template
   - Fill in the details

## Issue Templates

The following issue templates are available:

- **🐛 Bug Report** - Report bugs or unexpected behavior
- **✨ Feature Request** - Suggest new features or enhancements
- **📋 Engineering Standards Violation** - Report code quality or standards violations

## Next Steps

After running these scripts:

1. Review the created issues and adjust priorities as needed
2. Create GitHub milestones for version planning
3. Start working on high-priority issues
4. Use the issue templates for all new issues
