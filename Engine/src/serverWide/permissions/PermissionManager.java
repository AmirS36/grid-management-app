package serverWide.permissions;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class PermissionManager {

    // Stores sheet permissions: SheetName -> Map<Username, PermissionType>
    private final Map<String, Map<String, PermissionType>> permissionsMap = new ConcurrentHashMap<>();
    private final List<PermissionRequest> requestsList = new ArrayList<>();

    // Add or update a permission for a user on a specific sheet
    public void setPermission(String sheetName, String username, PermissionType permissionType) {
        permissionsMap
                .computeIfAbsent(sheetName, k -> new ConcurrentHashMap<>())
                .put(username, permissionType);
    }

    // Get permissions for a specific sheet
    public Map<String, PermissionType> getPermissions(String sheetName) {
        return permissionsMap.getOrDefault(sheetName, Collections.emptyMap());
    }

    // Get the permission type for a specific user on a specific sheet
    public PermissionType getPermission(String sheetName, String username) {
        // Get the map of users and their permissions for the given sheet
        Map<String, PermissionType> userPermissions = permissionsMap.get(sheetName);

        // If the sheet or the user isn't found, return PermissionType.NONE
        if (userPermissions == null || !userPermissions.containsKey(username)) {
            return PermissionType.NONE;
        }

        // Return the actual permission type for the user
        return userPermissions.get(username);
    }

    // Optional: Remove a user’s permission
    public void removePermission(String sheetName, String username) {
        Map<String, PermissionType> userPermissions = permissionsMap.get(sheetName);
        if (userPermissions != null) {
            userPermissions.remove(username);
        }
    }


    // Requests -----------------------------------------------
    // Request permission for a user on a specific sheet
    public void requestPermission(String sheetName, String requestingUser, PermissionType requestedPermission) {
        PermissionRequest newRequest = new PermissionRequest(sheetName, requestingUser, requestedPermission);
        requestsList.add(newRequest);
    }

    // Approve or reject a permission request
    public void handleRequest(PermissionRequest request, boolean approve) {
        if (approve) {
            setPermission(request.getSheetName(), request.getRequestingUser(), request.getRequestedPermission());
            request.setStatus(RequestStatus.APPROVED);
        } else {
            request.setStatus(RequestStatus.REJECTED);
        }
    }

    public List<PermissionRequest> getRequestsForSheet(String sheetName) {
        return requestsList.stream()
                .filter(request -> request.getSheetName().equals(sheetName))
                .collect(Collectors.toList());
    }

    // Find a specific permission request by sheet name and user
    public Optional<PermissionRequest> findRequest(String sheetName, String requestingUser, PermissionType requestedPermission) {
        return requestsList.stream()
                .filter(request -> request.getSheetName().equals(sheetName) &&
                        request.getRequestingUser().equals(requestingUser) &&
                        request.getRequestedPermission().equals(requestedPermission))
                .findFirst();
    }

    public void approveOrRejectRequest(String sheetName, String requestingUser, PermissionType requestedPermission, boolean approve) {
        Optional<PermissionRequest> optionalRequest = findRequest(sheetName, requestingUser, requestedPermission);

        if (optionalRequest.isPresent()) {
            PermissionRequest request = optionalRequest.get();
            handleRequest(request, approve);
        } else {
            System.out.println("Request not found for user: " + requestingUser);
        }
    }
}