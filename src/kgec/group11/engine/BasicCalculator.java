package kgec.group11.engine;

import kgec.group11.exception.CalculationException;
import kgec.group11.exception.DivisionByZeroException;
import kgec.group11.exception.InvalidMathDomainException;

public class BasicCalculator {
    protected double memoryValue;

    public BasicCalculator() {
        this.memoryValue = 0.0;
    }

    public double add(double a, double b) throws CalculationException {
        return validateFinite(a + b);
    }

    public double subtract(double a, double b) throws CalculationException {
        return validateFinite(a - b);
    }

    public double multiply(double a, double b) throws CalculationException {
        return validateFinite(a * b);
    }

    public double divide(double a, double b) throws CalculationException {
        if (b == 0.0) {
            throw new DivisionByZeroException("Cannot divide by zero.");
        }
        return validateFinite(a / b);
    }

    public double modulo(double a, double b) throws CalculationException {
        if (b == 0.0) {
            throw new DivisionByZeroException("Cannot compute modulo with zero divisor.");
        }
        return validateFinite(a % b);
    }

    public double power(double base, double exponent) throws CalculationException {
        if (base == 0.0 && exponent < 0.0) {
            throw new DivisionByZeroException("Zero cannot be raised to a negative power.");
        }
        if (base < 0.0 && Math.floor(exponent) != exponent) {
            throw new InvalidMathDomainException("Negative base with fractional exponent yields a non-real result.");
        }
        return validateFinite(Math.pow(base, exponent));
    }

    public double reciprocal(double value) throws CalculationException {
        if (value == 0.0) {
            throw new DivisionByZeroException("Reciprocal of zero is undefined.");
        }
        return validateFinite(1.0 / value);
    }

    public double percentage(double value) throws CalculationException {
        return validateFinite(value / 100.0);
    }

    public double negate(double value) {
        return value == 0.0 ? 0.0 : -value;
    }

    public double applyBinaryOperation(double left, double right, String operator) throws CalculationException {
        switch (operator) {
            case "+":
                return add(left, right);
            case "-":
                return subtract(left, right);
            case "*":
            case "×":
                return multiply(left, right);
            case "/":
            case "÷":
                return divide(left, right);
            case "%":
                return modulo(left, right);
            case "^":
                return power(left, right);
            default:
                throw new CalculationException("Unsupported operator: " + operator);
        }
    }

    public void memoryClear() {
        memoryValue = 0.0;
    }

    public double memoryRecall() {
        return memoryValue;
    }

    public void memoryAdd(double value) throws CalculationException {
        memoryValue = validateFinite(memoryValue + value);
    }

    public void memorySubtract(double value) throws CalculationException {
        memoryValue = validateFinite(memoryValue - value);
    }

    public boolean hasMemory() {
        return Math.abs(memoryValue) > 1e-12;
    }

    protected double validateFinite(double result) throws CalculationException {
        if (Double.isNaN(result)) {
            throw new InvalidMathDomainException("Result is undefined (NaN).");
        }
        if (Double.isInfinite(result)) {
            throw new CalculationException("Numeric overflow: result exceeds double precision bounds.");
        }
        return result;
    }
}
