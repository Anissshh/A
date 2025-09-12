#!/usr/bin/env sh
##############################################################################
## Gradle start up script for UN*X
##############################################################################

DIR="$( cd "$( dirname "$0" )" && pwd )"
APP_HOME="$DIR"
APP_NAME="Gradle"
APP_BASE_NAME=`basename "$0"`

# Add default JVM options here if you wish
DEFAULT_JVM_OPTS=""

CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar

exec java $DEFAULT_JVM_OPTS -cp "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"
