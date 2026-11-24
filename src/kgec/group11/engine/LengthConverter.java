package kgec.group11.engine;

import kgec.group11.exception.CalculationException;
import kgec.group11.exception.InvalidMathDomainException;
import kgec.group11.exception.InvalidUnitException;

public class LengthConverter extends AbstractUnitConverter {
    private static final String[] UNITS = {
        "Meter (m)",
        "Kilometer (km)",
        "Centimeter (cm)",
        "Millimeter (mm)",
        "Mile (mi)",
        "Yard (yd)",
        "Foot (ft)",
        "Inch (in)"
    };

    public LengthConverter() {
        super("Length", UNITS);
    }

    @Override
    public double convert(double value, String fromUnit, String toUnit) throws CalculationException {
        validateUnits(fromUnit, toUnit);
        if (value < 0.0) {
            throw new InvalidMathDomainException("Length cannot be negative.");
        }
        double inMeters = value * factorToMeters(fromUnit);
        return inMeters / factorToMeters(toUnit);
    }

    private double factorToMeters(String unit) throws InvalidUnitException {
        String normalized = unit.trim().toLowerCase();
        switch (normalized) {
            case "meter (m)":
            case "meter":
            case "m":
                return 1.0;
            case "kilometer (km)":
            case "kilometer":
            case "km":
                return 1000.0;
            case "centimeter (cm)":
            case "centimeter":
            case "cm":
                return 0.01;
            case "millimeter (mm)":
            case "millimeter":
            case "mm":
                return 0.001;
            case "mile (mi)":
            case "mile":
            case "mi":
                return 1609.344;
            case "yard (yd)":
            case "yard":
            case "yd":
                return 0.9144;
            case "foot (ft)":
            case "foot":
            case "ft":
                return 0.3048;
            case "inch (in)":
            case "inch":
            case "in":
                return 0.0254;
            default:
                throw new InvalidUnitException("Unknown length unit: " + unit);
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
