package kgec.group11.persistence;

import java.text.SimpleDateFormat;
import java.util.Date;

public class HistoryRecord {
    private static final String DELIMITER = " | ";
    private final String timestamp;
    private final String category;
    private final String expression;
    private final String result;

    public HistoryRecord(String category, String expression, String result) {
        this(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), category, expression, result);
    }

    public HistoryRecord(String timestamp, String category, String expression, String result) {
        this.timestamp = sanitize(timestamp);
        this.category = sanitize(category);
        this.expression = sanitize(expression);
        this.result = sanitize(result);
    }

    private static String sanitize(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("|", "/").replace("\r", " ").replace("\n", " ").trim();
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getCategory() {
        return category;
    }

    public String getExpression() {
        return expression;
    }

    public String getResult() {
        return result;
    }

    public String toFileLine() {
        return timestamp + DELIMITER + category + DELIMITER + expression + DELIMITER + result;
    }

    public static HistoryRecord fromFileLine(String line) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }
        String[] parts = line.split("\\s*\\|\\s*", 4);
        if (parts.length < 4) {
            return null;
        }
        return new HistoryRecord(parts[0], parts[1], parts[2], parts[3]);
    }

    @Override
    public String toString() {
        return "[" + timestamp + "] (" + category + ") " + expression + " = " + result;
    }
}
