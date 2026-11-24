@echo off
if not exist build mkdir build
dir /s /B src\*.java > sources.txt
javac -d build @sources.txt
del sources.txt
jar cfe Group11_CalculatorConverter.jar kgec.group11.app.MainApplication -C build .
java -cp build kgec.group11.app.MainApplication %*
