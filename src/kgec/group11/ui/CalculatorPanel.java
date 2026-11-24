package kgec.group11.ui;

import kgec.group11.engine.AngleMode;
import kgec.group11.engine.InputParser;
import kgec.group11.engine.ScientificCalculator;
import kgec.group11.exception.CalculationException;
import kgec.group11.persistence.HistoryRecord;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class CalculatorPanel extends BaseModulePanel implements ActionListener, KeyListener {
    private final ScientificCalculator calculator;
    private final HistoryUpdateListener historyListener;
    private final JTextField expressionField;
    private final JTextField displayField;
    private final JLabel angleModeBadge;
    private final JLabel memoryBadge;
    private final JButton angleToggleButton;

    private double firstOperand;
    private String pendingOperator;
    private boolean startNewInput;

    public CalculatorPanel(ScientificCalculator calculator, HistoryUpdateListener historyListener) {
        super();
        this.calculator = calculator;
        this.historyListener = historyListener;
        this.firstOperand = 0.0;
        this.pendingOperator = null;
        this.startNewInput = true;

        JPanel topCard = new JPanel(new BorderLayout(6, 6));
        topCard.setBackground(BG_CARD);
        topCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_LIGHT, 1),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));

        JPanel badgeBar = new JPanel(new BorderLayout());
        badgeBar.setBackground(BG_CARD);

        angleModeBadge = new JLabel("MODE: " + calculator.getAngleMode().getLabel());
        angleModeBadge.setFont(new Font("SansSerif", Font.BOLD, 12));
        angleModeBadge.setForeground(PRIMARY_ACCENT);

        memoryBadge = new JLabel("MEM: 0");
        memoryBadge.setFont(new Font("SansSerif", Font.BOLD, 12));
        memoryBadge.setForeground(TEXT_MUTED);

        badgeBar.add(angleModeBadge, BorderLayout.WEST);
        badgeBar.add(memoryBadge, BorderLayout.EAST);

        expressionField = createDisplayField(14, true);
        expressionField.setForeground(TEXT_MUTED);
        expressionField.setText("");
        expressionField.setToolTipText("Type a full mathematical expression (e.g., sin(30) + 2^3 * 4) and press Enter");
        expressionField.addKeyListener(this);

        displayField = createDisplayField(26, false);
        displayField.setText("0");

        JPanel fieldsStack = new JPanel(new GridLayout(2, 1, 0, 6));
        fieldsStack.setBackground(BG_CARD);
        fieldsStack.add(expressionField);
        fieldsStack.add(displayField);

        topCard.add(badgeBar, BorderLayout.NORTH);
        topCard.add(fieldsStack, BorderLayout.CENTER);

        JPanel buttonGrid = new JPanel(new GridLayout(7, 5, 8, 8));
        buttonGrid.setBackground(BG_SURFACE);

        angleToggleButton = createStyledButton(calculator.getAngleMode().getLabel(), PRIMARY_DARK, Color.WHITE, 13);
        angleToggleButton.setActionCommand("TOGGLE_ANGLE");
        angleToggleButton.addActionListener(this);

        String[][] rows = {
            {"TOGGLE_ANGLE", "MC", "MR", "M+", "M-"},
            {"sin", "cos", "tan", "asin", "acos"},
            {"atan", "log", "ln", "sqrt", "cbrt"},
            {"x^2", "x^3", "^", "n!", "1/x"},
            {"pi", "e", "C", "CE", "BACK"},
            {"7", "8", "9", "/", "%"},
            {"4", "5", "6", "*", "+/-"}
        };

        for (String[] row : rows) {
            for (String token : row) {
                if ("TOGGLE_ANGLE".equals(token)) {
                    buttonGrid.add(angleToggleButton);
                } else {
                    buttonGrid.add(buildGridButton(token));
                }
            }
        }

        JPanel bottomRowGrid = new JPanel(new GridLayout(2, 5, 8, 8));
        bottomRowGrid.setBackground(BG_SURFACE);
        String[] bottomTokens = {"1", "2", "3", "-", "|x|", "0", ".", "EXP", "+", "="};
        for (String token : bottomTokens) {
            bottomRowGrid.add(buildGridButton(token));
        }

        JPanel allButtons = new JPanel(new BorderLayout(8, 8));
        allButtons.setBackground(BG_SURFACE);
        allButtons.add(buttonGrid, BorderLayout.CENTER);
        allButtons.add(bottomRowGrid, BorderLayout.SOUTH);

        add(topCard, BorderLayout.NORTH);
        add(allButtons, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
    }

    private JButton buildGridButton(String token) {
        String label = token;
        Color bg = SCIENTIFIC_BG;
        Color fg = TEXT_PRIMARY;
        int fontSize = 14;

        if ("0123456789.".contains(token) && token.length() == 1) {
            bg = NUMBER_BG;
            fontSize = 16;
        } else if ("+".equals(token) || "-".equals(token) || "*".equals(token) || "/".equals(token) || "%".equals(token) || "^".equals(token)) {
            bg = OPERATOR_BG;
            fg = PRIMARY_DARK;
            fontSize = 16;
            if ("*".equals(token)) {
                label = "×";
            } else if ("/".equals(token)) {
                label = "÷";
            }
        } else if ("=".equals(token)) {
            bg = PRIMARY_ACCENT;
            fg = Color.WHITE;
            fontSize = 18;
        } else if ("C".equals(token) || "CE".equals(token) || "BACK".equals(token)) {
            bg = DANGER_BG;
            fg = DANGER_TEXT;
            if ("BACK".equals(token)) {
                label = "⌫";
            }
        } else if ("pi".equals(token)) {
            label = "π";
        } else if ("EXP".equals(token)) {
            label = "e^x";
        } else if ("sqrt".equals(token)) {
            label = "√x";
        } else if ("cbrt".equals(token)) {
            label = "∛x";
        }

        JButton button = createStyledButton(label, bg, fg, fontSize);
        button.setActionCommand(token);
        button.addActionListener(this);
        return button;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        try {
            if ("TOGGLE_ANGLE".equals(cmd)) {
                calculator.toggleAngleMode();
                updateAngleModeIndicators();
                showInfoStatus("Angle mode switched to " + calculator.getAngleMode().name());
                return;
            }
            if ("C".equals(cmd)) {
                resetModule();
                return;
            }
            if ("CE".equals(cmd)) {
                displayField.setText("0");
                startNewInput = true;
                showInfoStatus("Current entry cleared");
                return;
            }
            if ("BACK".equals(cmd)) {
                handleBackspace();
                return;
            }
            if ("MC".equals(cmd)) {
                calculator.memoryClear();
                updateMemoryIndicator();
                showInfoStatus("Memory cleared");
                return;
            }
            if ("MR".equals(cmd)) {
                displayField.setText(InputParser.formatNumber(calculator.memoryRecall()));
                startNewInput = true;
                showInfoStatus("Recalled value from memory");
                return;
            }
            if ("M+".equals(cmd)) {
                double val = InputParser.parseDouble(displayField.getText());
                calculator.memoryAdd(val);
                updateMemoryIndicator();
                showInfoStatus("Added " + InputParser.formatNumber(val) + " to memory");
                return;
            }
            if ("M-".equals(cmd)) {
                double val = InputParser.parseDouble(displayField.getText());
                calculator.memorySubtract(val);
                updateMemoryIndicator();
                showInfoStatus("Subtracted " + InputParser.formatNumber(val) + " from memory");
                return;
            }
            if ("pi".equals(cmd)) {
                displayField.setText(InputParser.formatNumber(Math.PI));
                startNewInput = true;
                showInfoStatus("Inserted constant π");
                return;
            }
            if ("e".equals(cmd)) {
                displayField.setText(InputParser.formatNumber(Math.E));
                startNewInput = true;
                showInfoStatus("Inserted constant e");
                return;
            }
            if ("0123456789.".contains(cmd) && cmd.length() == 1) {
                appendDigitOrDot(cmd);
                return;
            }
            if ("+".equals(cmd) || "-".equals(cmd) || "*".equals(cmd) || "/".equals(cmd) || "%".equals(cmd) || "^".equals(cmd)) {
                handleBinaryOperator(cmd);
                return;
            }
            if ("=".equals(cmd)) {
                handleEquals();
                return;
            }
            String unaryKey = "EXP".equals(cmd) ? "exp" : cmd;
            handleUnaryFunction(unaryKey);
        } catch (CalculationException ex) {
            displayField.setText("Error");
            startNewInput = true;
            pendingOperator = null;
            showErrorStatus(ex.getMessage());
        }
    }

    private void appendDigitOrDot(String token) {
        if (startNewInput || "Error".equals(displayField.getText())) {
            displayField.setText(".".equals(token) ? "0." : token);
            startNewInput = false;
        } else {
            String current = displayField.getText();
            if (".".equals(token) && current.contains(".")) {
                return;
            }
            displayField.setText(current + token);
        }
        showInfoStatus("Entering operand...");
    }

    private void handleBackspace() {
        if (startNewInput) {
            return;
        }
        String current = displayField.getText();
        if (current.length() <= 1 || (current.length() == 2 && current.startsWith("-"))) {
            displayField.setText("0");
            startNewInput = true;
        } else {
            displayField.setText(current.substring(0, current.length() - 1));
        }
    }

    private void handleBinaryOperator(String operator) throws CalculationException {
        double currentVal = InputParser.parseDouble(displayField.getText());
        if (pendingOperator != null && !startNewInput) {
            double intermediate = calculator.applyBinaryOperation(firstOperand, currentVal, pendingOperator);
            firstOperand = intermediate;
            displayField.setText(InputParser.formatNumber(intermediate));
        } else {
            firstOperand = currentVal;
        }
        pendingOperator = operator;
        expressionField.setText(InputParser.formatNumber(firstOperand) + " " + operator);
        startNewInput = true;
        showInfoStatus("Operator '" + operator + "' selected");
    }

    private void handleEquals() throws CalculationException {
        String typedExpr = expressionField.getText().trim();
        if (pendingOperator == null && !typedExpr.isEmpty() && !typedExpr.endsWith("=")) {
            evaluateTypedExpression(typedExpr);
            return;
        }
        if (pendingOperator == null) {
            return;
        }
        double secondOperand = InputParser.parseDouble(displayField.getText());
        double result = calculator.applyBinaryOperation(firstOperand, secondOperand, pendingOperator);
        String formattedExpr = InputParser.formatNumber(firstOperand) + " " + pendingOperator + " " + InputParser.formatNumber(secondOperand);
        String formattedRes = InputParser.formatNumber(result);
        expressionField.setText(formattedExpr + " =");
        displayField.setText(formattedRes);
        pendingOperator = null;
        startNewInput = true;
        recordHistory("Calculator", formattedExpr, formattedRes);
        showInfoStatus("Calculated successfully");
    }

    private void handleUnaryFunction(String func) throws CalculationException {
        double value = InputParser.parseDouble(displayField.getText());
        double result = calculator.applyUnaryFunction(func, value);
        String formattedExpr = func + "(" + InputParser.formatNumber(value) + ")";
        String formattedRes = InputParser.formatNumber(result);
        expressionField.setText(formattedExpr + " =");
        displayField.setText(formattedRes);
        startNewInput = true;
        recordHistory("Scientific (" + calculator.getAngleMode().getLabel() + ")", formattedExpr, formattedRes);
        showInfoStatus("Applied " + func);
    }

    private void evaluateTypedExpression(String rawExpression) throws CalculationException {
        double result = calculator.evaluateExpression(rawExpression);
        String formattedRes = InputParser.formatNumber(result);
        displayField.setText(formattedRes);
        expressionField.setText(rawExpression + " =");
        startNewInput = true;
        recordHistory("Expression (" + calculator.getAngleMode().getLabel() + ")", rawExpression, formattedRes);
        showInfoStatus("Expression evaluated");
    }

    private void recordHistory(String category, String expr, String res) {
        if (historyListener != null) {
            historyListener.onRecordAdded(new HistoryRecord(category, expr, res));
        }
    }

    public void updateAngleModeIndicators() {
        AngleMode mode = calculator.getAngleMode();
        angleModeBadge.setText("MODE: " + mode.getLabel());
        angleToggleButton.setText(mode.getLabel());
    }

    private void updateMemoryIndicator() {
        memoryBadge.setText("MEM: " + InputParser.formatNumber(calculator.memoryRecall()));
        memoryBadge.setForeground(calculator.hasMemory() ? PRIMARY_ACCENT : TEXT_MUTED);
    }

    @Override
    public void resetModule() {
        firstOperand = 0.0;
        pendingOperator = null;
        startNewInput = true;
        expressionField.setText("");
        displayField.setText("0");
        showInfoStatus("Calculator cleared");
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            String expr = expressionField.getText().trim();
            if (!expr.isEmpty()) {
                try {
                    evaluateTypedExpression(expr);
                } catch (CalculationException ex) {
                    displayField.setText("Error");
                    showErrorStatus(ex.getMessage());
                }
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
