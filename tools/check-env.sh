#!/usr/bin/env bash
set -euo pipefail

# Simple environment verification script for students
# Prints the versions of required tools and returns non-zero if a requirement is missing

REQUIRED=("java" "node" "npm" "git")

echo "Checking required tools..."
for t in "${REQUIRED[@]}"; do
  if ! command -v "$t" >/dev/null; then
    echo "ERROR: $t is not installed. Please install it and re-run." >&2
    exit 1
  fi
done

# Check g++ or clang++
if command -v g++ >/dev/null; then
  echo "g++: $(g++ --version | head -n1)"
elif command -v clang++ >/dev/null; then
  echo "clang++: $(clang++ --version | head -n1)"
else
  echo "WARNING: No C++ compiler found (g++ or clang++). C++ projects won't build." >&2
fi

# Print versions
java -version 2>&1 | head -n1
node -v
npm -v
git --version

# Check docker if present
if command -v docker >/dev/null; then
  docker --version
else
  echo "Docker not found. Optional if running postgres via Docker Docker-compose."
fi

echo "Environment check passed (note warnings above)."

exit 0
