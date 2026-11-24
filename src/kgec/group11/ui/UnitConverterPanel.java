package kgec.group11.ui;

import kgec.group11.engine.AbstractUnitConverter;
import kgec.group11.engine.ConverterFactory;
import kgec.group11.engine.InputParser;
import kgec.group11.exception.CalculationException;
import kgec.group11.persistence.HistoryRecord;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class UnitConverterPanel extends BaseModulePanel implements ActionListener, ItemListener {
    private final HistoryUpdateListener historyListener;
    private final JComboBox<String> categoryCombo;
    private final JComboBox<String> fromUnitCombo;
    private final JComboBox<String> toUnitCombo;
    private final JTextField inputField;
    private final JTextField resultField;
    private final JLabel summaryLabel;
    private final DefaultTableModel quickTableModel;

    private AbstractUnitConverter activeConverter;

    public UnitConverterPanel(HistoryUpdateListener historyListener) {
        super();
        this.historyListener = historyListener;

        JPanel formCard = new JPanel(new GridBagLayout());
        formCard.setBackground(BG_CARD);
        formCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_LIGHT, 1),
            BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Multi-Category Unit Converter (Length, Temperature, Weight)");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        titleLabel.setForeground(PRIMARY_DARK);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        formCard.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.weightx = 0.2;
        formCard.add(createFieldLabel("Category:"), gbc);

        categoryCombo = new JComboBox<>(ConverterFactory.getCategoryNames());
        categoryCombo.setFont(new Font("SansSerif", Font.PLAIN, 14));
        categoryCombo.addItemListener(this);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 0.8;
        formCard.add(categoryCombo, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.weightx = 0.2;
        formCard.add(createFieldLabel("Input Value:"), gbc);

        inputField = createDisplayField(18, true);
        inputField.setText("1");
        inputField.setActionCommand("CONVERT");
        inputField.addActionListener(this);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 0.8;
        formCard.add(inputField, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.weightx = 0.2;
        formCard.add(createFieldLabel("From Unit:"), gbc);

        fromUnitCombo = new JComboBox<>();
        fromUnitCombo.setFont(new Font("SansSerif", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 0.8;
        formCard.add(fromUnitCombo, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.weightx = 0.2;
        formCard.add(createFieldLabel("To Unit:"), gbc);

        toUnitCombo = new JComboBox<>();
        toUnitCombo.setFont(new Font("SansSerif", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 0.8;
        formCard.add(toUnitCombo, gbc);

        JPanel actionBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionBar.setBackground(BG_CARD);

        JButton convertBtn = createStyledButton("Convert", PRIMARY_ACCENT, Color.WHITE, 14);
        convertBtn.setActionCommand("CONVERT");
        convertBtn.addActionListener(this);

        JButton swapBtn = createStyledButton("Swap Units", OPERATOR_BG, PRIMARY_DARK, 14);
        swapBtn.setActionCommand("SWAP");
        swapBtn.addActionListener(this);

        JButton clearBtn = createStyledButton("Reset", DANGER_BG, DANGER_TEXT, 14);
        clearBtn.setActionCommand("CLEAR");
        clearBtn.addActionListener(this);

        actionBar.add(swapBtn);
        actionBar.add(clearBtn);
        actionBar.add(convertBtn);

        gbc.gridy = 5;
        gbc.gridx = 0;
        gbc.gridwidth = 3;
        formCard.add(actionBar, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 6;
        gbc.gridx = 0;
        gbc.weightx = 0.2;
        formCard.add(createFieldLabel("Result:"), gbc);

        resultField = createDisplayField(20, false);
        resultField.setForeground(PRIMARY_DARK);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 0.8;
        formCard.add(resultField, gbc);

        summaryLabel = new JLabel("Enter a value and click Convert.");
        summaryLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        summaryLabel.setForeground(TEXT_MUTED);
        gbc.gridy = 7;
        gbc.gridx = 0;
        gbc.gridwidth = 3;
        formCard.add(summaryLabel, gbc);

        quickTableModel = new DefaultTableModel(new String[]{"Target Unit", "Equivalent Value"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable quickTable = new JTable(quickTableModel);
        quickTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        quickTable.setRowHeight(24);
        quickTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));

        JScrollPane tableScroll = new JScrollPane(quickTable);
        tableScroll.setBorder(BorderFactory.createTitledBorder("All Equivalents for Current Input"));

        add(formCard, BorderLayout.NORTH);
        add(tableScroll, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);

        loadCategory((String) categoryCombo.getSelectedItem());
    }

    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 13));
        label.setForeground(TEXT_PRIMARY);
        return label;
    }

    private void loadCategory(String categoryName) {
        try {
            activeConverter = ConverterFactory.getConverter(categoryName);
            String[] units = activeConverter.getSupportedUnits();
            fromUnitCombo.removeAllItems();
            toUnitCombo.removeAllItems();
            for (String unit : units) {
                fromUnitCombo.addItem(unit);
                toUnitCombo.addItem(unit);
            }
            if (units.length > 1) {
                toUnitCombo.setSelectedIndex(1);
            }
            resultField.setText("");
            summaryLabel.setText("Category loaded: " + activeConverter.getCategoryName());
            quickTableModel.setRowCount(0);
            showInfoStatus("Selected " + activeConverter.getCategoryName() + " converter");
        } catch (CalculationException ex) {
            showErrorStatus(ex.getMessage());
        }
    }

    private void performConversion() {
        if (activeConverter == null) {
            return;
        }
        String rawInput = inputField.getText();
        String fromUnit = (String) fromUnitCombo.getSelectedItem();
        String toUnit = (String) toUnitCombo.getSelectedItem();
        try {
            double parsedInput = InputParser.parseDouble(rawInput);
            double converted = activeConverter.convertFromString(rawInput, fromUnit, toUnit);
            String formattedInput = InputParser.formatNumber(parsedInput);
            String formattedOutput = InputParser.formatNumber(converted);
            String fromSymbol = activeConverter.getUnitSymbol(fromUnit);
            String toSymbol = activeConverter.getUnitSymbol(toUnit);

            resultField.setText(formattedOutput + " " + toSymbol);
            summaryLabel.setText(formattedInput + " " + fromSymbol + " = " + formattedOutput + " " + toSymbol);
            populateEquivalentsTable(parsedInput, fromUnit);

            if (historyListener != null) {
                historyListener.onRecordAdded(new HistoryRecord(
                    "Converter (" + activeConverter.getCategoryName() + ")",
                    formattedInput + " " + fromSymbol + " -> " + toSymbol,
                    formattedOutput + " " + toSymbol
                ));
            }
            showInfoStatus("Converted successfully");
        } catch (CalculationException ex) {
            resultField.setText("Error");
            summaryLabel.setText(ex.getMessage());
            quickTableModel.setRowCount(0);
            showErrorStatus(ex.getMessage());
        }
    }

    private void populateEquivalentsTable(double value, String fromUnit) {
        quickTableModel.setRowCount(0);
        for (String unit : activeConverter.getSupportedUnits()) {
            try {
                double eq = activeConverter.convert(value, fromUnit, unit);
                quickTableModel.addRow(new Object[]{
                    unit,
                    InputParser.formatNumber(eq) + " " + activeConverter.getUnitSymbol(unit)
                });
            } catch (CalculationException ignored) {
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if ("CONVERT".equals(cmd)) {
            performConversion();
        } else if ("SWAP".equals(cmd)) {
            int fromIdx = fromUnitCombo.getSelectedIndex();
            int toIdx = toUnitCombo.getSelectedIndex();
            fromUnitCombo.setSelectedIndex(toIdx);
            toUnitCombo.setSelectedIndex(fromIdx);
            performConversion();
        } else if ("CLEAR".equals(cmd)) {
            resetModule();
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED && e.getSource() == categoryCombo) {
            loadCategory((String) categoryCombo.getSelectedItem());
        }
    }

    @Override
    public void resetModule() {
        inputField.setText("0");
        resultField.setText("");
        quickTableModel.setRowCount(0);
        summaryLabel.setText("Enter a value and click Convert.");
        showInfoStatus("Unit converter reset");
    }
}
