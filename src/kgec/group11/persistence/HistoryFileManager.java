package kgec.group11.persistence;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HistoryFileManager {
    private final File logFile;

    public HistoryFileManager(String filePath) {
        this.logFile = new File(filePath);
    }

    public File getLogFile() {
        return logFile;
    }

    public synchronized void appendRecord(HistoryRecord record) throws IOException {
        if (record == null) {
            return;
        }
        try {
            File parent = logFile.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true))) {
                writer.write(record.toFileLine());
                writer.newLine();
            }
        } catch (SecurityException se) {
            throw new IOException("File access restricted by security policy: " + se.getMessage(), se);
        }
    }

    public synchronized List<HistoryRecord> readAllRecords() throws IOException {
        List<HistoryRecord> records = new ArrayList<>();
        try {
            if (!logFile.exists()) {
                return records;
            }
            try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    HistoryRecord record = HistoryRecord.fromFileLine(line);
                    if (record != null) {
                        records.add(record);
                    }
                }
            }
        } catch (SecurityException se) {
            // When running inside a restricted sandbox (e.g. appletviewer without policy grants),
            // gracefully degrade to session memory rather than crashing initialization.
            return records;
        }
        return records;
    }

    public synchronized void clearHistory() throws IOException {
        try {
            if (logFile.exists()) {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, false))) {
                    writer.write("");
                }
            }
        } catch (SecurityException se) {
            throw new IOException("File access restricted by security policy: " + se.getMessage(), se);
        }
    }

    public synchronized void exportToFile(File destination) throws IOException {
        try {
            List<HistoryRecord> records = readAllRecords();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(destination, false))) {
                writer.write("KGEC CSE Group 11 - Calculator & Unit Converter History Log");
                writer.newLine();
                writer.write("============================================================");
                writer.newLine();
                for (HistoryRecord record : records) {
                    writer.write(record.toString());
                    writer.newLine();
                }
            }
        } catch (SecurityException se) {
            throw new IOException("File export restricted by security policy: " + se.getMessage(), se);
        }
    }
}
