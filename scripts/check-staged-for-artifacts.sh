#!/usr/bin/env bash
set -euo pipefail

# Check for common generated/build artifacts staged for commit
STAGED_FILES=$(git diff --cached --name-only)
BAD_PATTERNS=(
  "node_modules/"
  "dist/"
  "build/"
  "*.class"
  "*.jar"
)

for pattern in "${BAD_PATTERNS[@]}"; do
  if echo "$STAGED_FILES" | grep -q -E "${pattern//\*/.*}"; then
    echo "Error: Found staged files matching pattern: $pattern"
    echo "Please unstage build artifacts (node_modules, dist, build, *.class, *.jar) and try again."
    exit 1
  fi
done

exit 0
