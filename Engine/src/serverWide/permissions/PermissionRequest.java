package serverWide.permissions;

public class PermissionRequest {
    private final String sheetName;
    private final String requestingUser;
    private final PermissionType requestedPermission;
    private RequestStatus status;

    public PermissionRequest(String sheetName, String requestingUser, PermissionType requestedPermission) {
        this.sheetName = sheetName;
        this.requestingUser = requestingUser;
        this.requestedPermission = requestedPermission;
        this.status = RequestStatus.PENDING;
    }

    public String getSheetName() {
        return sheetName;
    }

    public String getRequestingUser() {
        return requestingUser;
    }

    public PermissionType getRequestedPermission() {
        return requestedPermission;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }
}
