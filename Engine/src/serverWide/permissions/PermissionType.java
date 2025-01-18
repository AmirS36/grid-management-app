package serverWide.permissions;

public enum PermissionType {
    OWNER("Owner"),
    READER("Reader"),
    WRITER("Writer"),
    NONE("None");

    private final String description;

    // Constructor to assign descriptions to each enum value
    PermissionType(String description) {
        this.description = description;
    }

    // Getter for the description
    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return description;
    }
}