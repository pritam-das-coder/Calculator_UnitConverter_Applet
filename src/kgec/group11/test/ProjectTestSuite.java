package kgec.group11.test;

import kgec.group11.engine.AbstractUnitConverter;
import kgec.group11.engine.AngleMode;
import kgec.group11.engine.ConverterFactory;
import kgec.group11.engine.InputParser;
import kgec.group11.engine.ScientificCalculator;
import kgec.group11.exception.CalculationException;
import kgec.group11.exception.DivisionByZeroException;
import kgec.group11.exception.InvalidInputException;
import kgec.group11.exception.InvalidMathDomainException;
import kgec.group11.exception.InvalidUnitException;
import kgec.group11.persistence.HistoryFileManager;
import kgec.group11.persistence.HistoryRecord;

import java.io.File;
import java.util.List;

public class ProjectTestSuite {
    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("=== KGEC CSE Group 11 - Automated Verification Test Suite ===");
        testArithmeticAndMemory();
        testTrigonometryAndScientific();
        testExpressionEvaluator();
        testUnitConverters();
        testWrapperClassParsingAndExceptions();
        testFilePersistence();

        System.out.println("=============================================================");
        System.out.println("Total Passed: " + passed + " | Total Failed: " + failed);
        if (failed > 0) {
            System.exit(1);
        }
    }

    private static void assertClose(String name, double expected, double actual, double eps) {
        if (Math.abs(expected - actual) <= eps) {
            passed++;
            System.out.println("[PASS] " + name + " -> " + actual);
        } else {
            failed++;
            System.out.println("[FAIL] " + name + " -> expected " + expected + ", got " + actual);
        }
    }

    private static void assertTrue(String name, boolean condition) {
        if (condition) {
            passed++;
            System.out.println("[PASS] " + name);
        } else {
            failed++;
            System.out.println("[FAIL] " + name);
        }
    }

    private static void testArithmeticAndMemory() {
        ScientificCalculator calc = new ScientificCalculator();
        try {
            assertClose("Addition (12.5 + 7.5)", 20.0, calc.add(12.5, 7.5), 1e-9);
            assertClose("Subtraction (50 - 18.25)", 31.75, calc.subtract(50, 18.25), 1e-9);
            assertClose("Multiplication (6 * 7)", 42.0, calc.multiply(6, 7), 1e-9);
            assertClose("Division (22 / 4)", 5.5, calc.divide(22, 4), 1e-9);
            assertClose("Modulo (17 % 5)", 2.0, calc.modulo(17, 5), 1e-9);
            assertClose("Power (2 ^ 10)", 1024.0, calc.power(2, 10), 1e-9);
            assertClose("Reciprocal (1 / 4)", 0.25, calc.reciprocal(4), 1e-9);

            calc.memoryAdd(25.0);
            calc.memorySubtract(10.0);
            assertClose("Memory M+ / M- (25 - 10)", 15.0, calc.memoryRecall(), 1e-9);
            calc.memoryClear();
            assertClose("Memory Clear", 0.0, calc.memoryRecall(), 1e-9);
        } catch (CalculationException ex) {
            failed++;
            System.out.println("[FAIL] Unexpected arithmetic error: " + ex.getMessage());
        }
    }

    private static void testTrigonometryAndScientific() {
        ScientificCalculator calc = new ScientificCalculator();
        try {
            calc.setAngleMode(AngleMode.DEGREES);
            assertClose("sin(30 deg)", 0.5, calc.sin(30), 1e-9);
            assertClose("cos(60 deg)", 0.5, calc.cos(60), 1e-9);
            assertClose("tan(45 deg)", 1.0, calc.tan(45), 1e-9);
            assertClose("asin(0.5) in deg", 30.0, calc.asin(0.5), 1e-9);
            assertClose("acos(0.5) in deg", 60.0, calc.acos(0.5), 1e-9);

            calc.setAngleMode(AngleMode.RADIANS);
            assertClose("sin(pi/2 rad)", 1.0, calc.sin(Math.PI / 2.0), 1e-9);
            assertClose("cos(pi rad)", -1.0, calc.cos(Math.PI), 1e-9);

            assertClose("sqrt(144)", 12.0, calc.sqrt(144), 1e-9);
            assertClose("cbrt(27)", 3.0, calc.cbrt(27), 1e-9);
            assertClose("log10(1000)", 3.0, calc.log10(1000), 1e-9);
            assertClose("ln(e^2)", 2.0, calc.ln(Math.E * Math.E), 1e-9);
            assertClose("factorial(5)", 120.0, calc.factorial(5), 1e-9);
        } catch (CalculationException ex) {
            failed++;
            System.out.println("[FAIL] Unexpected scientific error: " + ex.getMessage());
        }
    }

    private static void testExpressionEvaluator() {
        ScientificCalculator calc = new ScientificCalculator();
        calc.setAngleMode(AngleMode.DEGREES);
        try {
            assertClose("Expr: 2 + 3 * 4", 14.0, calc.evaluateExpression("2 + 3 * 4"), 1e-9);
            assertClose("Expr: (2 + 3) * 4", 20.0, calc.evaluateExpression("(2 + 3) * 4"), 1e-9);
            assertClose("Expr: sin(30) + cos(60) + 2^3", 9.0, calc.evaluateExpression("sin(30) + cos(60) + 2^3"), 1e-9);
            assertClose("Expr: sqrt(81) + 4!", 33.0, calc.evaluateExpression("sqrt(81) + 4!"), 1e-9);
        } catch (CalculationException ex) {
            failed++;
            System.out.println("[FAIL] Expression evaluator failed: " + ex.getMessage());
        }
    }

    private static void testUnitConverters() {
        try {
            AbstractUnitConverter length = ConverterFactory.getConverter("Length");
            assertClose("Length: 1 km -> m", 1000.0, length.convertFromString("1", "Kilometer (km)", "Meter (m)"), 1e-9);
            assertClose("Length: 12 in -> ft", 1.0, length.convertFromString("12", "Inch (in)", "Foot (ft)"), 1e-9);
            assertClose("Length: 1 mi -> km", 1.609344, length.convertFromString("1", "Mile (mi)", "Kilometer (km)"), 1e-6);

            AbstractUnitConverter temp = ConverterFactory.getConverter("Temperature");
            assertClose("Temp: 100 C -> F", 212.0, temp.convertFromString("100", "Celsius (°C)", "Fahrenheit (°F)"), 1e-9);
            assertClose("Temp: 32 F -> C", 0.0, temp.convertFromString("32", "Fahrenheit (°F)", "Celsius (°C)"), 1e-9);
            assertClose("Temp: 0 C -> K", 273.15, temp.convertFromString("0", "Celsius (°C)", "Kelvin (K)"), 1e-9);

            AbstractUnitConverter weight = ConverterFactory.getConverter("Weight");
            assertClose("Weight: 2.5 kg -> g", 2500.0, weight.convertFromString("2.5", "Kilogram (kg)", "Gram (g)"), 1e-9);
            assertClose("Weight: 16 oz -> lb", 1.0, weight.convertFromString("16", "Ounce (oz)", "Pound (lb)"), 1e-9);
        } catch (CalculationException ex) {
            failed++;
            System.out.println("[FAIL] Unit conversion error: " + ex.getMessage());
        }
    }

    private static void testWrapperClassParsingAndExceptions() {
        ScientificCalculator calc = new ScientificCalculator();

        try {
            calc.divide(10.0, 0.0);
            assertTrue("DivisionByZeroException thrown", false);
        } catch (DivisionByZeroException ex) {
            assertTrue("DivisionByZeroException thrown", true);
        } catch (CalculationException ex) {
            assertTrue("DivisionByZeroException thrown", false);
        }

        try {
            calc.tan(90.0);
            assertTrue("InvalidMathDomainException on tan(90 deg)", false);
        } catch (InvalidMathDomainException ex) {
            assertTrue("InvalidMathDomainException on tan(90 deg)", true);
        } catch (CalculationException ex) {
            assertTrue("InvalidMathDomainException on tan(90 deg)", false);
        }

        try {
            calc.sqrt(-9.0);
            assertTrue("InvalidMathDomainException on sqrt(-9)", false);
        } catch (InvalidMathDomainException ex) {
            assertTrue("InvalidMathDomainException on sqrt(-9)", true);
        } catch (CalculationException ex) {
            assertTrue("InvalidMathDomainException on sqrt(-9)", false);
        }

        try {
            InputParser.parseDouble("not_a_number");
            assertTrue("InvalidInputException on non-numeric wrapper parse", false);
        } catch (InvalidInputException ex) {
            assertTrue("InvalidInputException on non-numeric wrapper parse", true);
        }

        try {
            AbstractUnitConverter temp = ConverterFactory.getConverter("Temperature");
            temp.convert(-300.0, "Celsius (°C)", "Kelvin (K)");
            assertTrue("InvalidMathDomainException below absolute zero", false);
        } catch (InvalidMathDomainException ex) {
            assertTrue("InvalidMathDomainException below absolute zero", true);
        } catch (CalculationException ex) {
            assertTrue("InvalidMathDomainException below absolute zero", false);
        }

        try {
            ConverterFactory.getConverter("Speed");
            assertTrue("InvalidUnitException on unsupported category", false);
        } catch (InvalidUnitException ex) {
            assertTrue("InvalidUnitException on unsupported category", true);
        }
    }

    private static void testFilePersistence() {
        File tempLog = new File("test_calculator_history.txt");
        File tempExport = new File("test_exported_history.txt");
        try {
            if (tempLog.exists()) {
                tempLog.delete();
            }
            HistoryFileManager manager = new HistoryFileManager(tempLog.getPath());
            manager.appendRecord(new HistoryRecord("Calculator", "12 + 8", "20"));
            manager.appendRecord(new HistoryRecord("Converter (Temperature)", "100 °C -> °F", "212 °F"));

            List<HistoryRecord> loaded = manager.readAllRecords();
            assertTrue("File I/O loaded 2 records", loaded.size() == 2);
            assertTrue("File I/O record 1 matches", "20".equals(loaded.get(0).getResult()));
            assertTrue("File I/O record 2 matches", "212 °F".equals(loaded.get(1).getResult()));

            manager.exportToFile(tempExport);
            assertTrue("Exported history file exists and non-empty", tempExport.exists() && tempExport.length() > 0);

            manager.clearHistory();
            assertTrue("History cleared cleanly", manager.readAllRecords().isEmpty());
        } catch (Exception ex) {
            failed++;
            System.out.println("[FAIL] File persistence error: " + ex.getMessage());
        } finally {
            tempLog.delete();
            tempExport.delete();
        }
    }
}
