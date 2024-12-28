#!/bin/sh
chmod +x gradlew
BASE_DIR=$(dirname "$0")
BASE_DIR=$(cd "$BASE_DIR" && pwd)
exec "$BASE_DIR/gradle/wrapper/gradle-wrapper.jar" "$@"
