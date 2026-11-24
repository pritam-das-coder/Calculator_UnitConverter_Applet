package kgec.group11.engine;

import kgec.group11.exception.CalculationException;
import kgec.group11.exception.InvalidUnitException;

public abstract class AbstractUnitConverter {
    private final String categoryName;
    private final String[] supportedUnits;

    protected AbstractUnitConverter(String categoryName, String[] supportedUnits) {
        this.categoryName = categoryName;
        this.supportedUnits = supportedUnits.clone();
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String[] getSupportedUnits() {
        return supportedUnits.clone();
    }

    public boolean isUnitSupported(String unit) {
        if (unit == null) {
            return false;
        }
        for (String supported : supportedUnits) {
            if (supported.equalsIgnoreCase(unit.trim())) {
                return true;
            }
        }
        return false;
    }

    protected void validateUnits(String fromUnit, String toUnit) throws InvalidUnitException {
        if (!isUnitSupported(fromUnit)) {
            throw new InvalidUnitException("Unsupported source unit for " + categoryName + ": " + fromUnit);
        }
        if (!isUnitSupported(toUnit)) {
            throw new InvalidUnitException("Unsupported target unit for " + categoryName + ": " + toUnit);
        }
    }

    public double convertFromString(String rawInput, String fromUnit, String toUnit) throws CalculationException {
        double numericValue = InputParser.parseDouble(rawInput);
        return convert(numericValue, fromUnit, toUnit);
    }

    public abstract double convert(double value, String fromUnit, String toUnit) throws CalculationException;

    public abstract String getUnitSymbol(String unit);
}
