package logic;

public enum Team {
    BLUE,
    RED;

    public boolean isBlue() {
        return this == BLUE;
    }

    public boolean isRed() {
        return this == RED;
    }
}
