package kgec.group11.engine;

import kgec.group11.exception.InvalidInputException;
import java.text.DecimalFormat;

public final class InputParser {
    private static final DecimalFormat STANDARD_FORMAT = new DecimalFormat("0.##########");
    private static final DecimalFormat SCIENTIFIC_FORMAT = new DecimalFormat("0.######E0");

    private InputParser() {
    }

    public static double parseDouble(String rawText) throws InvalidInputException {
        if (rawText == null || rawText.trim().isEmpty()) {
            throw new InvalidInputException("Input cannot be empty.");
        }
        try {
            Double parsedValue = Double.valueOf(Double.parseDouble(rawText.trim()));
            if (parsedValue.isNaN() || parsedValue.isInfinite()) {
                throw new InvalidInputException("Numeric value is out of finite bounds.");
            }
            return parsedValue.doubleValue();
        } catch (NumberFormatException ex) {
            throw new InvalidInputException("Invalid numeric format: \"" + rawText.trim() + "\"", ex);
        }
    }

    public static int parseNonNegativeInt(double value) throws InvalidInputException {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            throw new InvalidInputException("Value must be a finite number.");
        }
        if (value < 0 || Math.floor(value) != value) {
            throw new InvalidInputException("Factorial requires a non-negative integer.");
        }
        if (value > 170) {
            throw new InvalidInputException("Factorial input exceeds maximum double range (170).");
        }
        return Integer.valueOf((int) value).intValue();
    }

    public static String formatNumber(double value) {
        if (Double.isNaN(value)) {
            return "NaN";
        }
        if (Double.isInfinite(value)) {
            return value > 0 ? "Infinity" : "-Infinity";
        }
        if (Math.abs(value) < 1e-12) {
            return "0";
        }
        double absVal = Math.abs(value);
        if (absVal >= 1e12 || absVal < 1e-6) {
            return SCIENTIFIC_FORMAT.format(value);
        }
        return STANDARD_FORMAT.format(value);
    }
}
