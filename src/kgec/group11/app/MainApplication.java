package kgec.group11.app;

import kgec.group11.engine.AngleMode;
import kgec.group11.ui.MainWorkspacePanel;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.BorderLayout;
import java.awt.Dimension;

public class MainApplication extends JFrame {
    public MainApplication(String initialTab, AngleMode angleMode, String historyFile) {
        super("Group 11 - Interactive Graphical Calculator & Unit Converter");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        MainWorkspacePanel workspacePanel = new MainWorkspacePanel(historyFile, angleMode);
        workspacePanel.selectTabByName(initialTab);
        add(workspacePanel, BorderLayout.CENTER);

        setMinimumSize(new Dimension(560, 660));
        setSize(620, 720);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        String initialTab = "calculator";
        AngleMode angleMode = AngleMode.DEGREES;
        String historyFile = "calculator_history.txt";

        for (String arg : args) {
            if (arg.startsWith("--tab=")) {
                initialTab = arg.substring("--tab=".length());
            } else if (arg.startsWith("--angle=")) {
                String modeArg = arg.substring("--angle=".length());
                angleMode = "RAD".equalsIgnoreCase(modeArg) ? AngleMode.RADIANS : AngleMode.DEGREES;
            } else if (arg.startsWith("--history=")) {
                historyFile = arg.substring("--history=".length());
            }
        }

        final String finalTab = initialTab;
        final AngleMode finalMode = angleMode;
        final String finalHistory = historyFile;

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ignored) {
            }
            MainApplication frame = new MainApplication(finalTab, finalMode, finalHistory);
            frame.setVisible(true);
        });
    }
}
