package kgec.group11.engine;

import kgec.group11.exception.CalculationException;
import kgec.group11.exception.InvalidMathDomainException;
import kgec.group11.exception.InvalidUnitException;

public class WeightConverter extends AbstractUnitConverter {
    private static final String[] UNITS = {
        "Kilogram (kg)",
        "Gram (g)",
        "Milligram (mg)",
        "Metric Ton (t)",
        "Pound (lb)",
        "Ounce (oz)"
    };

    public WeightConverter() {
        super("Weight", UNITS);
    }

    @Override
    public double convert(double value, String fromUnit, String toUnit) throws CalculationException {
        validateUnits(fromUnit, toUnit);
        if (value < 0.0) {
            throw new InvalidMathDomainException("Weight cannot be negative.");
        }
        double inKilograms = value * factorToKilograms(fromUnit);
        return inKilograms / factorToKilograms(toUnit);
    }

    private double factorToKilograms(String unit) throws InvalidUnitException {
        String normalized = unit.trim().toLowerCase();
        switch (normalized) {
            case "kilogram (kg)":
            case "kilogram":
            case "kg":
                return 1.0;
            case "gram (g)":
            case "gram":
            case "g":
                return 0.001;
            case "milligram (mg)":
            case "milligram":
            case "mg":
                return 0.000001;
            case "metric ton (t)":
            case "metric ton":
            case "t":
                return 1000.0;
            case "pound (lb)":
            case "pound":
            case "lb":
                return 0.45359237;
            case "ounce (oz)":
            case "ounce":
            case "oz":
                return 0.028349523125;
            default:
                throw new InvalidUnitException("Unknown weight unit: " + unit);
        }
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
