package kgec.group11.ui;

import kgec.group11.persistence.HistoryFileManager;
import kgec.group11.persistence.HistoryRecord;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class HistoryPanel extends BaseModulePanel implements ActionListener {
    private final HistoryFileManager fileManager;
    private final DefaultTableModel tableModel;

    public HistoryPanel(HistoryFileManager fileManager) {
        super();
        this.fileManager = fileManager;

        JPanel headerBar = new JPanel(new BorderLayout());
        headerBar.setBackground(BG_SURFACE);

        JLabel title = new JLabel("Saved History (" + fileManager.getLogFile().getName() + ")");
        title.setFont(new Font("SansSerif", Font.BOLD, 14));
        title.setForeground(PRIMARY_DARK);

        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttonBar.setBackground(BG_SURFACE);

        JButton refreshBtn = createStyledButton("Refresh", OPERATOR_BG, PRIMARY_DARK, 12);
        refreshBtn.setActionCommand("REFRESH");
        refreshBtn.addActionListener(this);

        JButton exportBtn = createStyledButton("Export Log", PRIMARY_ACCENT, Color.WHITE, 12);
        exportBtn.setActionCommand("EXPORT");
        exportBtn.addActionListener(this);

        JButton clearBtn = createStyledButton("Clear History", DANGER_BG, DANGER_TEXT, 12);
        clearBtn.setActionCommand("CLEAR");
        clearBtn.addActionListener(this);

        buttonBar.add(refreshBtn);
        buttonBar.add(exportBtn);
        buttonBar.add(clearBtn);

        headerBar.add(title, BorderLayout.WEST);
        headerBar.add(buttonBar, BorderLayout.EAST);

        tableModel = new DefaultTableModel(new String[]{"Timestamp", "Module", "Expression", "Result"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(tableModel);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.setRowHeight(24);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_LIGHT, 1));

        add(headerBar, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);

        reloadHistory();
    }

    public void appendAndPersist(HistoryRecord record) {
        if (record == null) {
            return;
        }
        tableModel.insertRow(0, new Object[]{
            record.getTimestamp(),
            record.getCategory(),
            record.getExpression(),
            record.getResult()
        });
        try {
            fileManager.appendRecord(record);
            showInfoStatus("Saved to " + fileManager.getLogFile().getName());
        } catch (IOException | SecurityException ex) {
            showErrorStatus("In-memory only (File restricted: " + ex.getMessage() + ")");
        }
    }

    public void reloadHistory() {
        tableModel.setRowCount(0);
        try {
            List<HistoryRecord> records = fileManager.readAllRecords();
            for (int i = records.size() - 1; i >= 0; i--) {
                HistoryRecord r = records.get(i);
                tableModel.addRow(new Object[]{
                    r.getTimestamp(),
                    r.getCategory(),
                    r.getExpression(),
                    r.getResult()
                });
            }
            showInfoStatus("Loaded " + records.size() + " history record(s)");
        } catch (IOException | SecurityException ex) {
            showErrorStatus("Failed to read history file: " + ex.getMessage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if ("REFRESH".equals(cmd)) {
            reloadHistory();
        } else if ("CLEAR".equals(cmd)) {
            resetModule();
        } else if ("EXPORT".equals(cmd)) {
            try {
                JFileChooser chooser = new JFileChooser();
                chooser.setSelectedFile(new File("group11_exported_history.txt"));
                int result = chooser.showSaveDialog(this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    try {
                        fileManager.exportToFile(chooser.getSelectedFile());
                        showInfoStatus("Exported log to " + chooser.getSelectedFile().getName());
                    } catch (IOException | SecurityException ex) {
                        showErrorStatus("Export failed: " + ex.getMessage());
                    }
                }
            } catch (SecurityException ex) {
                showErrorStatus("File chooser access restricted: " + ex.getMessage());
            }
        }
    }

    @Override
    public void resetModule() {
        try {
            fileManager.clearHistory();
            tableModel.setRowCount(0);
            showInfoStatus("History log cleared");
        } catch (IOException | SecurityException ex) {
            tableModel.setRowCount(0);
            showInfoStatus("History cleared in memory (file restricted)");
        }
    }
}
