package kgec.group11.engine;

public enum AngleMode {
    DEGREES("DEG"),
    RADIANS("RAD");

    private final String label;

    AngleMode(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
