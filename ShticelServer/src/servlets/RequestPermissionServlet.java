package servlets;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import serverWide.permissions.PermissionManager;
import serverWide.permissions.PermissionType;
import serverWide.sheetsTable.SheetsTable;
import utils.ServletUtils;
import utils.SessionUtils;

import java.io.IOException;


@WebServlet(name = "RequestPermissionServlet", urlPatterns = {"/requestPermission"})
public class RequestPermissionServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String usernameFromSession = SessionUtils.getUsername(request);
        String sheetName = request.getParameter("sheetName");
        String permissionTypeString = request.getParameter("permissionType");

        if (sheetName == null || permissionTypeString == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"message\":\"Missing parameters\"}");
            return;
        }

        try {
            SheetsTable sheetsTable = ServletUtils.getSheetsTable(getServletContext());
            PermissionManager permissionManager = sheetsTable.getPermissionManager();

            PermissionType permissionType = PermissionType.valueOf(permissionTypeString.toUpperCase());

            permissionManager.requestPermission(sheetName, usernameFromSession, permissionType);

            // Process the permission request...
            System.out.println("Processing request for sheet: " + sheetName + " with permission: " + permissionType);

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("{\"message\":\"Request processed successfully\"}");
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"message\":\"Invalid permission type\"}");
        }
    }
}