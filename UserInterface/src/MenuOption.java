public enum MenuOption {

    LOAD_SHEET(1),
    SHOW_SHEET(2),
    SHOW_CELL_VALUE(3),
    UPDATE_CELL_VALUE(4),
    SHOW_VERSIONS(5),
    SAVE_SHEET_TO_FILE(6),
    LOAD_SHEET_FROM_FILE(7),
    EXIT(8);

    private final int value;

    MenuOption(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static MenuOption fromValue(int value) {
        for (MenuOption option : MenuOption.values()) {
            if (option.getValue() == value) {
                return option;
            }
        }
        throw new IllegalArgumentException("Invalid value: " + value);
    }
}
