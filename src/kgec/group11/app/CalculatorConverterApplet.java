package kgec.group11.app;

import kgec.group11.engine.AngleMode;
import kgec.group11.ui.MainWorkspacePanel;

import javax.swing.JApplet;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.BorderLayout;

@SuppressWarnings("deprecation")
public class CalculatorConverterApplet extends JApplet {
    private MainWorkspacePanel workspacePanel;

    @Override
    public void init() {
        final String initialTab = safeGetParameter("initialTab", "calculator");
        final String angleParam = safeGetParameter("angleMode", "DEG");
        final String historyFile = safeGetParameter("historyFile", "calculator_history.txt");

        AngleMode mode = "RAD".equalsIgnoreCase(angleParam) ? AngleMode.RADIANS : AngleMode.DEGREES;

        try {
            SwingUtilities.invokeAndWait(() -> {
                try {
                    UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                } catch (Exception ignored) {
                }
                setLayout(new BorderLayout());
                workspacePanel = new MainWorkspacePanel(historyFile, mode);
                workspacePanel.selectTabByName(initialTab);
                add(workspacePanel, BorderLayout.CENTER);
            });
        } catch (Exception ex) {
            throw new RuntimeException("Failed to initialize CalculatorConverterApplet", ex);
        }
    }

    private String safeGetParameter(String name, String defaultValue) {
        try {
            String value = getParameter(name);
            return (value == null || value.trim().isEmpty()) ? defaultValue : value.trim();
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }

    @Override
    public void destroy() {
    }
}
