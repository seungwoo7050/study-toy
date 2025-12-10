#!/usr/bin/env bash
set -euo pipefail

# Check staged files for common build artifacts
# This script is intended to be used as a pre-commit hook (e.g., .husky/pre-commit)
# to block accidental commits of generated files that should be ignored (node_modules, dist, build etc.).
# Usage:
#   - Run manually: `./scripts/check-staged-for-artifacts.sh` (must be invoked from the repository root)
#   - It uses the git index (staged files) as the source of truth.
# Exit codes:
#   - 0: OK, no problematic files staged
#   - 1: Problematic files found (script will exit 1 and can be used to block the commit)

# Acquire a list of staged files (added or modified) from git index
STAGED_FILES=$(git diff --cached --name-status --diff-filter=AM | awk '{print $2}')

# Patterns to reject (simple globs)
BAD_PATTERNS=(
  "node_modules/"
  "dist/"
  "build/"
  "*.class"
  "*.jar"
)

for pattern in "${BAD_PATTERNS[@]}"; do
  # Convert glob to a basic regex for a simple grep match
  if echo "$STAGED_FILES" | grep -q -E "${pattern//\*/.*}"; then
    echo "Error: Found staged files matching pattern: $pattern"
    echo "Please unstage build artifacts (node_modules, dist, build, *.class, *.jar) and try again."
    exit 1
  fi
done

# Educational check: ensure newly staged shell scripts include a small header with `Purpose:` or `Usage:` text
for f in $STAGED_FILES; do
  if [[ "$f" == *.sh ]]; then
    # Get staged file content from index (not from working tree) to validate header
    if git show :"$f" 2>/dev/null | head -n 20 | grep -q -E "Purpose:|Usage:|\[FILE\]|\[LEARN\]"; then
      true
    else
      echo "Error: Shell script $f does not include a header comment with Purpose:/Usage:."
      echo "Please add a short header with Purpose/Usage lines to help learners (see DOCS/SCRIPTS.md)."
      exit 1
    fi
  fi
done

exit 0

# Acquire a list of staged files (added or modified) from git index
STAGED_FILES=$(git diff --cached --name-status --diff-filter=AM | awk '{print $2}')

# Patterns to reject (simple globs)
BAD_PATTERNS=(
  "node_modules/"
  "dist/"
  "build/"
  "*.class"
  "*.jar"
)

for pattern in "${BAD_PATTERNS[@]}"; do
  # Convert glob to a basic regex for a simple grep match
  if echo "$STAGED_FILES" | grep -q -E "${pattern//\*/.*}"; then
    echo "Error: Found staged files matching pattern: $pattern"
    echo "Please unstage build artifacts (node_modules, dist, build, *.class, *.jar) and try again."
    exit 1
  fi
done

# Educational check: ensure newly staged shell scripts include a small header with `Purpose:` or `Usage:` text
for f in $STAGED_FILES; do
  if [[ "$f" == *.sh ]]; then
    # Get staged file content from index (not from working tree) to validate header
    if git show :"$f" 2>/dev/null | head -n 20 | grep -q -E "Purpose:|Usage:|\[FILE\]|\[LEARN\]"; then
      true
    else
      echo "Error: Shell script $f does not include a header comment with Purpose:/Usage:."
      echo "Please add a short header with Purpose/Usage lines to help learners (see DOCS/SCRIPTS.md)."
      exit 1
    fi
  fi
done

exit 0
