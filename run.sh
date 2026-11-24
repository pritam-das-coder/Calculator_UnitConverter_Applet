#!/usr/bin/env bash
set -e
mkdir -p build
find src -name "*.java" > sources.txt
javac -d build @sources.txt
rm -f sources.txt
jar cfe Group11_CalculatorConverter.jar kgec.group11.app.MainApplication -C build .
java -cp build kgec.group11.app.MainApplication "$@"
