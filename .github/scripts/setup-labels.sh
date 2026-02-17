#!/bin/bash
# Script to create GitHub labels from labels.yml
# Requires: GitHub CLI (gh) installed and authenticated
# Usage: ./setup-labels.sh

set -e

REPO="pmurasky/LiftIt-Java"
LABELS_FILE=".github/labels.yml"

echo "🏷️  Setting up GitHub labels for $REPO"
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

# Check if labels.yml exists
if [ ! -f "$LABELS_FILE" ]; then
    echo "❌ Labels file not found: $LABELS_FILE"
    exit 1
fi

echo "📋 Using npx github-label-sync to sync labels..."
echo ""

# Use github-label-sync to create/update labels from YAML
npx -y github-label-sync --labels "$LABELS_FILE" "$REPO"

echo ""
echo "✅ Labels setup complete!"
echo "View labels at: https://github.com/$REPO/labels"
