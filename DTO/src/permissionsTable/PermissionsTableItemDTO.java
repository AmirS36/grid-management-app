package permissionsTable;


public class PermissionsTableItemDTO {

    private String userName;
    private String requestedPermission;
    private String requestStatus;

    public PermissionsTableItemDTO(String userName, String accessLevel, String requestStatus) {
        this.userName = userName;
        this.requestedPermission = accessLevel;
        this.requestStatus = requestStatus;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAccessLevel() {
        return requestedPermission;
    }

    public String getAccessLevelAsString() {
        return requestedPermission;
    }

    public void setAccessLevel(String accessLevel) {
        this.requestedPermission = accessLevel;
    }

    public String getRequestStatus() {
        return requestStatus;
    }

    public String getRequestStatusAsString() {
        return requestStatus;
    }

    public void setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
    }
}
