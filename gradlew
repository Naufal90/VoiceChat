#!/bin/sh
# Gradle Wrapper Script for Unix-like systems
BASE_DIR=$(dirname "$0")
BASE_DIR=$(cd "$BASE_DIR" && pwd)
exec "$BASE_DIR/gradle/wrapper/gradle-wrapper.jar" "$@"
