package kgec.group11.ui;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Insets;

public abstract class BaseModulePanel extends JPanel {
    protected static final Color BG_SURFACE = new Color(244, 246, 249);
    protected static final Color BG_CARD = Color.WHITE;
    protected static final Color PRIMARY_DARK = new Color(19, 78, 74);
    protected static final Color PRIMARY_ACCENT = new Color(15, 118, 110);
    protected static final Color OPERATOR_BG = new Color(224, 242, 241);
    protected static final Color SCIENTIFIC_BG = new Color(238, 242, 255);
    protected static final Color NUMBER_BG = new Color(255, 255, 255);
    protected static final Color DANGER_BG = new Color(254, 226, 226);
    protected static final Color DANGER_TEXT = new Color(185, 28, 28);
    protected static final Color TEXT_PRIMARY = new Color(15, 23, 42);
    protected static final Color TEXT_MUTED = new Color(71, 85, 105);
    protected static final Color BORDER_LIGHT = new Color(203, 213, 225);

    protected final JLabel statusLabel;

    protected BaseModulePanel() {
        super(new BorderLayout(12, 12));
        setBackground(BG_SURFACE);
        setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        statusLabel = new JLabel("Ready");
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        statusLabel.setForeground(TEXT_MUTED);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(4, 2, 0, 2));
    }

    protected JButton createStyledButton(String text, Color background, Color foreground, int fontSize) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, fontSize));
        button.setBackground(background);
        button.setForeground(foreground);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_LIGHT, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        button.setMargin(new Insets(4, 6, 4, 6));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    protected JTextField createDisplayField(int fontSize, boolean editable) {
        JTextField field = new JTextField();
        field.setFont(new Font("Monospaced", Font.BOLD, fontSize));
        field.setHorizontalAlignment(JTextField.RIGHT);
        field.setEditable(editable);
        field.setBackground(BG_CARD);
        field.setForeground(TEXT_PRIMARY);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_LIGHT, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return field;
    }

    protected void showInfoStatus(String message) {
        statusLabel.setForeground(PRIMARY_DARK);
        statusLabel.setText(message);
    }

    protected void showErrorStatus(String message) {
        statusLabel.setForeground(DANGER_TEXT);
        statusLabel.setText("Error: " + message);
    }

    public abstract void resetModule();
}
