#!/bin/bash

# D'abord, on determine le systeme d'exploitation
OS="$(uname)"
if [[ "$OS" == "Darwin" ]]; then
  PLATFORM_FLAGS="-Wall -g -framework OpenGL -framework GLUT -fPIC -lm -lc -shared"
else
# Sinon on suppose que c'est Linux
  PLATFORM_FLAGS="-Wall -g -lGL -lGLU -lglut -fPIC -lm -lc -shared"
fi


export FLAGS="$PLATFORM_FLAGS"

make "${@:-all}"
