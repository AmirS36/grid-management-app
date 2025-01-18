package serverWide.permissions;

public enum RequestStatus {
    PENDING("Pending"),
    APPROVED("Approved"),
    REJECTED("Rejected"),
    NONE("");

    private final String description;

    // Constructor to assign descriptions to each enum value
    RequestStatus(String description) {
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