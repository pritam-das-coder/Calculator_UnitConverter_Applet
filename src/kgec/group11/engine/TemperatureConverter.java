package kgec.group11.engine;

import kgec.group11.exception.CalculationException;
import kgec.group11.exception.InvalidMathDomainException;
import kgec.group11.exception.InvalidUnitException;

public class TemperatureConverter extends AbstractUnitConverter {
    private static final String[] UNITS = {
        "Celsius (°C)",
        "Fahrenheit (°F)",
        "Kelvin (K)"
    };

    public TemperatureConverter() {
        super("Temperature", UNITS);
    }

    @Override
    public double convert(double value, String fromUnit, String toUnit) throws CalculationException {
        validateUnits(fromUnit, toUnit);
        double celsius = toCelsius(value, fromUnit);
        if (celsius < -273.15 - 1e-9) {
            throw new InvalidMathDomainException("Temperature cannot be below absolute zero (-273.15 °C / 0 K).");
        }
        return fromCelsius(celsius, toUnit);
    }

    private double toCelsius(double value, String unit) throws InvalidUnitException {
        String normalized = unit.trim().toLowerCase();
        if (normalized.startsWith("celsius")) {
            return value;
        }
        if (normalized.startsWith("fahrenheit")) {
            return (value - 32.0) * 5.0 / 9.0;
        }
        if (normalized.startsWith("kelvin")) {
            return value - 273.15;
        }
        throw new InvalidUnitException("Unknown temperature unit: " + unit);
    }

    private double fromCelsius(double celsius, String unit) throws InvalidUnitException {
        String normalized = unit.trim().toLowerCase();
        if (normalized.startsWith("celsius")) {
            return celsius;
        }
        if (normalized.startsWith("fahrenheit")) {
            return (celsius * 9.0 / 5.0) + 32.0;
        }
        if (normalized.startsWith("kelvin")) {
            return celsius + 273.15;
        }
        throw new InvalidUnitException("Unknown temperature unit: " + unit);
    }

    @Override
    public String getUnitSymbol(String unit) {
        if (unit == null) {
            return "";
        }
        int open = unit.indexOf('(');
        int close = unit.indexOf(')');
        if (open >= 0 && close > open) {
            return unit.substring(open + 1, close);
        }
        return unit;
    }
}
