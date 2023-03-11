package org.shyiko.client_server.final_project.util;

public enum Command {
    CREATE_PRODUCT(0), READ_PRODUCT(1), UPDATE_PRODUCT(2), DELETE_PRODUCT(3), LIST_PRODUCT(4),
    CREATE_GROUP(5), READ_GROUP(6), UPDATE_GROUP(7), DELETE_GROUP(8), LIST_GROUP(9),
    INCREMENT_PRODUCT(10), DECREMENT_PRODUCT(11), TOTAL_IN_GROUPS(12), TOTAL_IN_STORAGE(13);

    private final int value;

    Command(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static Command getCommandByCode(int x) {
        return switch (x) {
            case 0 -> CREATE_PRODUCT;
            case 1 -> READ_PRODUCT;
            case 2 -> UPDATE_PRODUCT;
            case 3 -> DELETE_PRODUCT;
            case 4 -> LIST_PRODUCT;
            case 5 -> CREATE_GROUP;
            case 6 -> READ_GROUP;
            case 7 -> UPDATE_GROUP;
            case 8 -> DELETE_GROUP;
            case 9 -> LIST_GROUP;
            case 10 -> INCREMENT_PRODUCT;
            case 11 -> DECREMENT_PRODUCT;
            case 12 -> TOTAL_IN_GROUPS;
            case 13 -> TOTAL_IN_STORAGE;
            default -> null;
        };
    }
}
