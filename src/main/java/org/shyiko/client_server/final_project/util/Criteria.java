package org.shyiko.client_server.final_project.util;

public enum Criteria {
    ID(0), NAME(1), AMOUNT(2), PRICE(3), GROUP(4);
    private final int value;

    Criteria(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static Criteria getCommandByCode(int x) {
        return switch (x) {
            case 0 -> ID;
            case 1 -> NAME;
            case 2 -> AMOUNT;
            case 3 -> PRICE;
            case 4 -> GROUP;
            default -> null;
        };
    }
}
