#!/usr/bin/env sh
##############################################################################
## Gradle start up script for UN*X
##############################################################################

DIR="$( cd "$( dirname "$0" )" && pwd )"
java -Xmx64m -cp "$DIR/gradle/wrapper/gradle-wrapper.jar" org.gradle.wrapper.GradleWrapperMain "$@"
