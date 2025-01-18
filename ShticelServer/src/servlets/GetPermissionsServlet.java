package servlets;

import com.google.gson.Gson;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import permissionsTable.PermissionsTableItemDTO;
import serverWide.permissions.PermissionManager;
import serverWide.permissions.PermissionRequest;
import serverWide.sheetsTable.SheetsTable;
import utils.ServletUtils;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;


@WebServlet(name = "GetPermissionsServlet", urlPatterns = {"/getPermissions"})
public class GetPermissionsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Get the sheet name from the request
        String sheetName = request.getParameter("sheetName");

        // Get the SheetsTable from the servlet context
        SheetsTable sheetsTable = ServletUtils.getSheetsTable(getServletContext());

        // Retrieve the PermissionManager
        PermissionManager permissionManager = sheetsTable.getPermissionManager();

        // Get the list of permission requests for the given sheet
        List<PermissionRequest> requests = permissionManager.getRequestsForSheet(sheetName);

        // Convert the permission requests to DTOs
        List<PermissionsTableItemDTO> requestDTOs = requests.stream()
                .map(req -> new PermissionsTableItemDTO(
                        req.getRequestingUser(),
                        req.getRequestedPermission().toString(),
                        req.getStatus().toString()
                ))
                .collect(Collectors.toList());

        // Convert the list of DTOs to JSON and send it in the response
        response.getWriter().write(new Gson().toJson(requestDTOs));
    }
}