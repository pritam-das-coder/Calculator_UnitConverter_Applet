# Interactive Graphical Calculator and Unit Converter Applet

**Institution:** Kalyani Government Engineering College, Department of Computer Science and Engineering  
**Course:** Object-Oriented Programming Lab (PCC-CS593) | Academic Year: 2026–2027 (Odd Semester)  
**Group:** Group 11  
**Members:**
1. Pritam Das
2. Sanket Sau
3. Soumyadip Dey
4. Satyanarayan Banerjee
5. Rakesh Dolui

**Faculty Supervisors:** Anup Kumar Biswas, Manju Biswas, Kuntal Bhowmick (Assistant Professors)

## Features Implemented
- **Scientific & Arithmetic Calculator:** Basic arithmetic (`+`, `-`, `×`, `÷`, `%`, `^`, `1/x`, `+/-`), trigonometric & inverse trigonometric functions (`sin`, `cos`, `tan`, `asin`, `acos`, `atan`) with `DEG`/`RAD` toggle, logarithms (`log10`, `ln`), roots (`sqrt`, `cbrt`), powers (`x^2`, `x^3`, `e^x`), factorial (`n!`), constants (`π`, `e`), memory registers (`MC`, `MR`, `M+`, `M-`), and a full recursive-descent mathematical expression evaluator.
- **Multi-Category Unit Converter:** Converts **Length** (m, km, cm, mm, mi, yd, ft, in), **Temperature** (°C, °F, K with absolute-zero validation), and **Weight** (kg, g, mg, t, lb, oz) with unit swap and an instant all-equivalents reference table.
- **Flat-File History Persistence:** Automatically logs calculations and conversions to a local flat text file (`calculator_history.txt`) with reload, export, and clear actions (no external database used).
- **Dual Execution Modes:** Runs as a standalone Swing/AWT desktop application (`MainApplication` extending `JFrame`) on JDK 8–21+ and as a classic Java Applet (`CalculatorConverterApplet` extending `JApplet`) via `appletviewer` on JDK 8.

## Package & OOP Architecture
- `kgec.group11.exception` — Custom exception hierarchy (`CalculationException`, `DivisionByZeroException`, `InvalidMathDomainException`, `InvalidInputException`, `InvalidUnitException`).
- `kgec.group11.engine` — `BasicCalculator` → `ScientificCalculator` (inheritance), `AbstractUnitConverter` → `LengthConverter`, `TemperatureConverter`, `WeightConverter` (abstraction & polymorphism), `ConverterFactory`, `AngleMode`, and `InputParser` (wrapper classes `Double.parseDouble()`, `Double.valueOf()`, `Integer.valueOf()`).
- `kgec.group11.persistence` — `HistoryRecord` and `HistoryFileManager` (flat-file I/O via `BufferedReader`, `BufferedWriter`, `FileReader`, `FileWriter`).
- `kgec.group11.ui` — `BaseModulePanel` → `CalculatorPanel`, `UnitConverterPanel`, `HistoryPanel` (GUI inheritance & layout management with `ActionListener`, `ItemListener`, `KeyListener`), `MainWorkspacePanel`, `HistoryUpdateListener`.
- `kgec.group11.app` — `CalculatorConverterApplet` (`JApplet`) and `MainApplication` (`JFrame` with command-line arguments `--tab=calc|converter|history`, `--angle=DEG|RAD`, `--history=<file>`).
- `kgec.group11.test` — `ProjectTestSuite` (standalone automated verification suite).

## How to Run

### Option 1: Run the Pre-Built Executable JAR (Fastest)
```bash
java -jar Group11_CalculatorConverter.jar
```
Optional command-line arguments:
```bash
java -jar Group11_CalculatorConverter.jar --tab=converter --angle=RAD
```

### Option 2: Compile & Run from Source
- **Linux / macOS:** `./run.sh`
- **Windows:** `run.bat`
- **Manual:**
  ```bash
  mkdir -p build
  javac -d build $(find src -name "*.java")
  java -cp build kgec.group11.app.MainApplication
  ```

### Option 3: Run as a Java Applet (JDK 8 `appletviewer`)
```bash
appletviewer CalculatorConverterApplet.html
```
Or explicitly passing the included policy file:
```bash
appletviewer -J-Djava.security.policy=applet.policy CalculatorConverterApplet.html
```
*(Note: A local `applet.policy` is provided in the repository and `~/.java.policy` is configured to grant file permissions for history logging).*

### Option 4: Run the Automated Unit Test Suite
```bash
java -cp Group11_CalculatorConverter.jar kgec.group11.test.ProjectTestSuite
```
