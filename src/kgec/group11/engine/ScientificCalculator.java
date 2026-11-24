package kgec.group11.engine;

import kgec.group11.exception.CalculationException;
import kgec.group11.exception.InvalidInputException;
import kgec.group11.exception.InvalidMathDomainException;

public class ScientificCalculator extends BasicCalculator {
    private AngleMode angleMode;

    public ScientificCalculator() {
        super();
        this.angleMode = AngleMode.DEGREES;
    }

    public AngleMode getAngleMode() {
        return angleMode;
    }

    public void setAngleMode(AngleMode angleMode) {
        if (angleMode != null) {
            this.angleMode = angleMode;
        }
    }

    public void toggleAngleMode() {
        this.angleMode = (this.angleMode == AngleMode.DEGREES) ? AngleMode.RADIANS : AngleMode.DEGREES;
    }

    private double toRadians(double angle) {
        return angleMode == AngleMode.DEGREES ? Math.toRadians(angle) : angle;
    }

    private double fromRadians(double radians) {
        return angleMode == AngleMode.DEGREES ? Math.toDegrees(radians) : radians;
    }

    public double sin(double value) throws CalculationException {
        double radians = toRadians(value);
        double res = Math.sin(radians);
        return validateFinite(Math.abs(res) < 1e-12 ? 0.0 : res);
    }

    public double cos(double value) throws CalculationException {
        double radians = toRadians(value);
        double res = Math.cos(radians);
        return validateFinite(Math.abs(res) < 1e-12 ? 0.0 : res);
    }

    public double tan(double value) throws CalculationException {
        double radians = toRadians(value);
        if (Math.abs(Math.cos(radians)) < 1e-12) {
            throw new InvalidMathDomainException("Tangent is undefined at odd multiples of 90 degrees (pi/2 rad).");
        }
        double res = Math.tan(radians);
        return validateFinite(Math.abs(res) < 1e-12 ? 0.0 : res);
    }

    public double asin(double value) throws CalculationException {
        if (value < -1.0 || value > 1.0) {
            throw new InvalidMathDomainException("arcsin(x) requires -1 <= x <= 1.");
        }
        return validateFinite(fromRadians(Math.asin(value)));
    }

    public double acos(double value) throws CalculationException {
        if (value < -1.0 || value > 1.0) {
            throw new InvalidMathDomainException("arccos(x) requires -1 <= x <= 1.");
        }
        return validateFinite(fromRadians(Math.acos(value)));
    }

    public double atan(double value) throws CalculationException {
        return validateFinite(fromRadians(Math.atan(value)));
    }

    public double sqrt(double value) throws CalculationException {
        if (value < 0.0) {
            throw new InvalidMathDomainException("Square root of a negative number is not real.");
        }
        return validateFinite(Math.sqrt(value));
    }

    public double cbrt(double value) throws CalculationException {
        return validateFinite(Math.cbrt(value));
    }

    public double square(double value) throws CalculationException {
        return validateFinite(value * value);
    }

    public double cube(double value) throws CalculationException {
        return validateFinite(value * value * value);
    }

    public double log10(double value) throws CalculationException {
        if (value <= 0.0) {
            throw new InvalidMathDomainException("Base-10 logarithm requires a strictly positive input.");
        }
        return validateFinite(Math.log10(value));
    }

    public double ln(double value) throws CalculationException {
        if (value <= 0.0) {
            throw new InvalidMathDomainException("Natural logarithm requires a strictly positive input.");
        }
        return validateFinite(Math.log(value));
    }

    public double exp(double value) throws CalculationException {
        return validateFinite(Math.exp(value));
    }

    public double abs(double value) {
        return Math.abs(value);
    }

    public double factorial(double value) throws CalculationException {
        int n = InputParser.parseNonNegativeInt(value);
        double result = 1.0;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return validateFinite(result);
    }

    public double applyUnaryFunction(String functionName, double value) throws CalculationException {
        switch (functionName) {
            case "sin":
                return sin(value);
            case "cos":
                return cos(value);
            case "tan":
                return tan(value);
            case "asin":
                return asin(value);
            case "acos":
                return acos(value);
            case "atan":
                return atan(value);
            case "sqrt":
                return sqrt(value);
            case "cbrt":
                return cbrt(value);
            case "x^2":
                return square(value);
            case "x^3":
                return cube(value);
            case "log":
                return log10(value);
            case "ln":
                return ln(value);
            case "exp":
                return exp(value);
            case "1/x":
                return reciprocal(value);
            case "n!":
                return factorial(value);
            case "|x|":
                return abs(value);
            case "%":
                return percentage(value);
            case "+/-":
                return negate(value);
            default:
                throw new CalculationException("Unsupported function: " + functionName);
        }
    }

    public double evaluateExpression(String expression) throws CalculationException {
        if (expression == null || expression.trim().isEmpty()) {
            throw new InvalidInputException("Expression cannot be empty.");
        }
        String normalized = expression.replace('×', '*').replace('÷', '/').replace("π", "pi");
        ExpressionParser parser = new ExpressionParser(normalized, this);
        double result = parser.parse();
        return validateFinite(result);
    }

    private static class ExpressionParser {
        private final String input;
        private final ScientificCalculator calculator;
        private int pos;
        private int ch;

        ExpressionParser(String input, ScientificCalculator calculator) {
            this.input = input;
            this.calculator = calculator;
            this.pos = -1;
            nextChar();
        }

        private void nextChar() {
            pos++;
            ch = (pos < input.length()) ? input.charAt(pos) : -1;
        }

        private boolean eat(int charToEat) {
            while (ch == ' ') {
                nextChar();
            }
            if (ch == charToEat) {
                nextChar();
                return true;
            }
            return false;
        }

        double parse() throws CalculationException {
            double x = parseExpression();
            while (ch == ' ') {
                nextChar();
            }
            if (pos < input.length()) {
                throw new InvalidInputException("Unexpected character in expression: '" + (char) ch + "'");
            }
            return x;
        }

        private double parseExpression() throws CalculationException {
            double x = parseTerm();
            while (true) {
                if (eat('+')) {
                    x = calculator.add(x, parseTerm());
                } else if (eat('-')) {
                    x = calculator.subtract(x, parseTerm());
                } else {
                    return x;
                }
            }
        }

        private double parseTerm() throws CalculationException {
            double x = parsePower();
            while (true) {
                if (eat('*')) {
                    x = calculator.multiply(x, parsePower());
                } else if (eat('/')) {
                    x = calculator.divide(x, parsePower());
                } else if (eat('%')) {
                    x = calculator.modulo(x, parsePower());
                } else {
                    return x;
                }
            }
        }

        private double parsePower() throws CalculationException {
            double x = parseUnary();
            if (eat('^')) {
                x = calculator.power(x, parsePower());
            }
            return x;
        }

        private double parseUnary() throws CalculationException {
            if (eat('+')) {
                return parseUnary();
            }
            if (eat('-')) {
                return calculator.negate(parseUnary());
            }
            return parseFactor();
        }

        private double parseFactor() throws CalculationException {
            while (ch == ' ') {
                nextChar();
            }
            double x;
            int startPos = this.pos;
            if (eat('(')) {
                x = parseExpression();
                if (!eat(')')) {
                    throw new InvalidInputException("Missing closing parenthesis ')' in expression.");
                }
            } else if ((ch >= '0' && ch <= '9') || ch == '.') {
                while ((ch >= '0' && ch <= '9') || ch == '.') {
                    nextChar();
                }
                if (ch == 'E' || ch == 'e') {
                    int expPos = this.pos;
                    nextChar();
                    if (ch == '+' || ch == '-') {
                        nextChar();
                    }
                    if (ch >= '0' && ch <= '9') {
                        while (ch >= '0' && ch <= '9') {
                            nextChar();
                        }
                    } else {
                        this.pos = expPos;
                        this.ch = input.charAt(expPos);
                    }
                }
                x = InputParser.parseDouble(input.substring(startPos, this.pos));
            } else if (Character.isLetter(ch)) {
                while (Character.isLetterOrDigit(ch)) {
                    nextChar();
                }
                String token = input.substring(startPos, this.pos).toLowerCase();
                if (token.equals("pi")) {
                    x = Math.PI;
                } else if (token.equals("e")) {
                    x = Math.E;
                } else {
                    double arg;
                    if (eat('(')) {
                        arg = parseExpression();
                        if (!eat(')')) {
                            throw new InvalidInputException("Missing closing parenthesis after function '" + token + "'.");
                        }
                    } else {
                        arg = parseUnary();
                    }
                    x = evaluateNamedFunction(token, arg);
                }
            } else {
                throw new InvalidInputException("Invalid token near position " + (pos + 1));
            }

            while (eat('!')) {
                x = calculator.factorial(x);
            }
            return x;
        }

        private double evaluateNamedFunction(String func, double arg) throws CalculationException {
            switch (func) {
                case "sin":
                    return calculator.sin(arg);
                case "cos":
                    return calculator.cos(arg);
                case "tan":
                    return calculator.tan(arg);
                case "asin":
                    return calculator.asin(arg);
                case "acos":
                    return calculator.acos(arg);
                case "atan":
                    return calculator.atan(arg);
                case "sqrt":
                    return calculator.sqrt(arg);
                case "cbrt":
                    return calculator.cbrt(arg);
                case "log":
                case "log10":
                    return calculator.log10(arg);
                case "ln":
                    return calculator.ln(arg);
                case "exp":
                    return calculator.exp(arg);
                case "abs":
                    return calculator.abs(arg);
                default:
                    throw new InvalidInputException("Unknown function: " + func);
            }
        }
    }
}
