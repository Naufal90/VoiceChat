#!/bin/bash
# gradlew - Gradle wrapper script for UNIX-like systems.

DIR="$( cd "$( dirname "$0" )" && pwd )"
DISTRIBUTION_URL=https\://services.gradle.org/distributions/gradle-8.12-all.zip
GRADLE_HOME=$DIR/gradle

if [ ! -d "$GRADLE_HOME" ]; then
  echo "Downloading Gradle distribution..."
  curl -L $DISTRIBUTION_URL | tar -xz -C $DIR
fi

"$GRADLE_HOME"/bin/gradle "$@"
