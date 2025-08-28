#!/bin/bash
# Script to run the Spring Boot application with automatic restart on code changes
# Requires: gradlew, entr (install via `brew install entr` on macOS)

# Find all relevant source and resource files
find ./src/main -type f \( -name '*.kt' -o -name '*.java' -o -name '*.yml' -o -name '*.properties' \) | \
entr -r ./gradlew bootRun
