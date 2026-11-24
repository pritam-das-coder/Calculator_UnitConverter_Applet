package kgec.group11.ui;

import kgec.group11.engine.AngleMode;
import kgec.group11.engine.ScientificCalculator;
import kgec.group11.persistence.HistoryFileManager;
import kgec.group11.persistence.HistoryRecord;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

public class MainWorkspacePanel extends JPanel implements HistoryUpdateListener {
    private final ScientificCalculator calculator;
    private final CalculatorPanel calculatorPanel;
    private final UnitConverterPanel converterPanel;
    private final HistoryPanel historyPanel;
    private final JTabbedPane tabbedPane;

    public MainWorkspacePanel(String historyFilePath, AngleMode initialAngleMode) {
        super(new BorderLayout());
        setBackground(new Color(244, 246, 249));

        this.calculator = new ScientificCalculator();
        if (initialAngleMode != null) {
            this.calculator.setAngleMode(initialAngleMode);
        }

        HistoryFileManager fileManager = new HistoryFileManager(historyFilePath);
        this.historyPanel = new HistoryPanel(fileManager);
        this.calculatorPanel = new CalculatorPanel(calculator, this);
        this.converterPanel = new UnitConverterPanel(this);

        JPanel headerPanel = new JPanel(new BorderLayout(4, 2));
        headerPanel.setBackground(new Color(19, 78, 74));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        JLabel titleLabel = new JLabel("Interactive Graphical Calculator & Unit Converter");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("KGEC CSE | PCC-CS593 OOP Lab | Group 11");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(204, 251, 241));

        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("SansSerif", Font.BOLD, 13));
        tabbedPane.addTab("Scientific Calculator", calculatorPanel);
        tabbedPane.addTab("Unit Converter", converterPanel);
        tabbedPane.addTab("History Log", historyPanel);

        add(headerPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }

    public void selectTabByName(String tabName) {
        if (tabName == null) {
            return;
        }
        String lower = tabName.trim().toLowerCase();
        if (lower.startsWith("conv")) {
            tabbedPane.setSelectedIndex(1);
        } else if (lower.startsWith("hist")) {
            tabbedPane.setSelectedIndex(2);
        } else {
            tabbedPane.setSelectedIndex(0);
        }
    }

    @Override
    public void onRecordAdded(HistoryRecord record) {
        historyPanel.appendAndPersist(record);
    }
}
