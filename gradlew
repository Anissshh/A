#!/bin/sh
##############################################################################
## Gradle start up script for UN*X
##############################################################################

DIR="$( cd "$( dirname "$0" )" && pwd )"
JAVA_EXEC="$(which java)"

exec "$JAVA_EXEC" -cp "$DIR/gradle/wrapper/gradle-wrapper.jar" org.gradle.wrapper.GradleWrapperMain "$@"
