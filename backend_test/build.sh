#!/bin/bash

# D'abord, on determine le systeme d'exploitation
OS="$(uname)"

if [[ "$OS" == "Darwin" ]]; then
  # macOS
  JAVA_HOME=$(/usr/libexec/java_home)
  JNI_FLAGS="-I${JAVA_HOME}/include -I${JAVA_HOME}/include/darwin -fPIC"
  PLATFORM_FLAGS="-Wall -g -framework OpenGL -framework GLUT -fPIC -lm -lc -shared"
else
  # Sinon on suppose que c'est Linux
  JNI_FLAGS="-I/usr/lib/jvm/java-21-openjdk-amd64/include -I/usr/lib/jvm/java-21-openjdk-amd64/include/linux -fPIC"
  PLATFORM_FLAGS="-Wall -g -lGL -lGLU -lglut -fPIC -lm -lc -shared"
fi


export FLAGS="$PLATFORM_FLAGS"
export JNI_FLAGS="$JNI_FLAGS"

make "${@:-all}"
