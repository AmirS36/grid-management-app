package servlets;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import serverWide.permissions.PermissionManager;
import serverWide.permissions.PermissionType;
import serverWide.sheetsTable.SheetsTable;
import utils.ServletUtils;

import java.io.IOException;


@WebServlet(name = "AppDecPermissionServlet", urlPatterns = {"/approveDeclinePermission"})
public class AppDecPermissionServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String sheetName = request.getParameter("sheetName");
        String requestedBy = request.getParameter("requestedBy");
        String permissionTypeString = request.getParameter("requestedPermission");
        String isApprovedString = request.getParameter("isApproved");

        if (sheetName == null || requestedBy == null || permissionTypeString == null || isApprovedString == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"message\":\"Missing parameters\"}");
            return;
        }

        PermissionType permissionType = PermissionType.valueOf(permissionTypeString.toUpperCase());
        boolean isApproved = Boolean.parseBoolean(isApprovedString);
        SheetsTable sheetsTable = ServletUtils.getSheetsTable(getServletContext());
        PermissionManager permissionManager = sheetsTable.getPermissionManager();

        permissionManager.approveOrRejectRequest(sheetName, requestedBy, permissionType, isApproved);

        response.setStatus(HttpServletResponse.SC_OK);
    }
}