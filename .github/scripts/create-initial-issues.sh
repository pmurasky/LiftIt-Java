#!/bin/bash
# Script to create initial GitHub issues
# Requires: GitHub CLI (gh) installed and authenticated
# Usage: ./create-initial-issues.sh

set -e

REPO="pmurasky/LiftIt-Java"

echo "📝 Creating initial issues for $REPO"
echo ""

# Check if gh is installed
if ! command -v gh &> /dev/null; then
    echo "❌ GitHub CLI (gh) is not installed."
    echo "Install it with: sudo apt install gh"
    echo "Or visit: https://cli.github.com/"
    exit 1
fi

# Check if authenticated
if ! gh auth status &> /dev/null; then
    echo "❌ Not authenticated with GitHub CLI."
    echo "Run: gh auth login"
    exit 1
fi

# Function to create an issue
create_issue() {
    local title="$1"
    local body="$2"
    local labels="$3"
    
    echo "Creating: $title"
    gh issue create \
        --repo "$REPO" \
        --title "$title" \
        --body "$body" \
        --label "$labels"
}

echo "Creating documentation issues..."

create_issue \
    "Expand README with Architecture Overview" \
    "## Description
The current README is minimal. Expand it to include:
- Project vision and goals
- Architecture overview (current and planned)
- Contribution guidelines reference
- Link to engineering standards
- Development workflow overview

## Acceptance Criteria
- [ ] README includes project vision
- [ ] Architecture section added
- [ ] Links to AGENTS.md and engineering standards
- [ ] Development setup instructions are clear" \
    "documentation,priority-medium,effort-small"

create_issue \
    "Add CHANGELOG.md for Version Tracking" \
    "## Description
Add a CHANGELOG.md following the [Keep a Changelog](https://keepachangelog.com/) format to track version history and changes.

## Acceptance Criteria
- [ ] CHANGELOG.md created with proper format
- [ ] Includes sections: Added, Changed, Deprecated, Removed, Fixed, Security
- [ ] Documents version 0.1.0-SNAPSHOT" \
    "documentation,priority-low,effort-small"

create_issue \
    "Document Engineering Standards Workflow" \
    "## Description
Create a contributor-friendly guide explaining how to work with the engineering standards in this project, including:
- TDD micro-commit workflow
- How to use the pre-commit checklist
- Common patterns and examples
- How AI agents should interact with the standards

## Acceptance Criteria
- [ ] New doc in \`docs/\` explaining the workflow
- [ ] Examples of good commit messages
- [ ] Links to relevant engineering-standards files
- [ ] Quick reference guide for developers" \
    "documentation,engineering-standards,priority-medium,effort-medium"

echo ""
echo "Creating CI/CD issues..."

create_issue \
    "Add Code Coverage Reporting to CI" \
    "## Description
Integrate JaCoCo code coverage reporting into the CI pipeline and publish coverage reports.

## Acceptance Criteria
- [ ] JaCoCo configured in build.gradle
- [ ] Coverage report generated on every CI run
- [ ] Coverage threshold enforced (80% minimum)
- [ ] Coverage badge added to README" \
    "ci-cd,testing,priority-high,effort-medium"

create_issue \
    "Add Checkstyle for Java Style Enforcement" \
    "## Description
Add Checkstyle to enforce Java coding standards automatically in the CI pipeline.

## Acceptance Criteria
- [ ] Checkstyle plugin added to build.gradle
- [ ] Custom checkstyle.xml configuration created
- [ ] CI fails on style violations
- [ ] Aligned with engineering standards (15-line method limit, etc.)" \
    "ci-cd,code-quality,priority-high,effort-medium"

create_issue \
    "Add SpotBugs for Static Analysis" \
    "## Description
Integrate SpotBugs for static code analysis to catch potential bugs early.

## Acceptance Criteria
- [ ] SpotBugs plugin added to build.gradle
- [ ] CI runs SpotBugs on every build
- [ ] Configuration excludes false positives
- [ ] CI fails on high-priority bugs" \
    "ci-cd,code-quality,priority-medium,effort-medium"

create_issue \
    "Add Dependency Vulnerability Scanning" \
    "## Description
Add automated dependency vulnerability scanning using OWASP Dependency-Check or GitHub's Dependabot.

## Acceptance Criteria
- [ ] Dependabot enabled for the repository
- [ ] Vulnerability alerts configured
- [ ] Auto-update PRs for security patches
- [ ] CI includes dependency check" \
    "ci-cd,dependencies,priority-high,effort-small"

echo ""
echo "Creating code quality issues..."

create_issue \
    "Configure JaCoCo for Test Coverage Metrics" \
    "## Description
Set up JaCoCo to track and enforce test coverage metrics locally and in CI.

## Acceptance Criteria
- [ ] JaCoCo plugin configured
- [ ] Coverage reports generated in HTML and XML
- [ ] Minimum coverage threshold: 80%
- [ ] Critical paths require 100% coverage" \
    "testing,code-quality,priority-high,effort-small"

create_issue \
    "Add Pre-Commit Hooks for Local Quality Checks" \
    "## Description
Implement pre-commit hooks to run quality checks before commits, ensuring every commit is production-ready.

## Acceptance Criteria
- [ ] Pre-commit hook script created
- [ ] Runs: tests, Checkstyle, SpotBugs
- [ ] Prevents commits if checks fail
- [ ] Documentation on how to install/use" \
    "code-quality,engineering-standards,priority-medium,effort-medium"

create_issue \
    "Set Up Automated Dependency Updates" \
    "## Description
Configure Dependabot or Renovate to automatically create PRs for dependency updates.

## Acceptance Criteria
- [ ] Dependabot/Renovate configured
- [ ] Weekly update schedule
- [ ] Auto-merge for patch updates
- [ ] Grouped updates for related dependencies" \
    "dependencies,chore,priority-low,effort-small"

echo ""
echo "Creating feature issues..."

create_issue \
    "Define Project Roadmap and Milestones" \
    "## Description
Create a project roadmap defining the vision for LiftIt-Java beyond being a starter template. What should this project become?

## Acceptance Criteria
- [ ] Roadmap document created
- [ ] GitHub milestones created for versions
- [ ] Feature priorities defined
- [ ] Timeline estimates provided" \
    "enhancement,documentation,priority-high,effort-medium"

create_issue \
    "Add Example Domain Model Implementation" \
    "## Description
Add an example domain model implementation demonstrating SOLID principles and the engineering standards in practice.

## Acceptance Criteria
- [ ] Example domain chosen (e.g., Task Manager, Library)
- [ ] Domain model classes follow SOLID principles
- [ ] 100% test coverage for domain model
- [ ] Documentation explaining design decisions" \
    "enhancement,priority-medium,effort-large"

echo ""
echo "Creating testing issues..."

create_issue \
    "Increase Test Coverage Baseline to 80%" \
    "## Description
Establish and enforce an 80% test coverage baseline for the project.

## Acceptance Criteria
- [ ] Current coverage measured
- [ ] Tests added to reach 80% coverage
- [ ] JaCoCo enforces 80% minimum
- [ ] Critical paths have 100% coverage" \
    "testing,code-quality,priority-high,effort-medium"

echo ""
echo "✅ All initial issues created successfully!"
echo "View issues at: https://github.com/$REPO/issues"
