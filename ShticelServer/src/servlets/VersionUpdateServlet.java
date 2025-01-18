package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import serverWide.sheetsTable.SheetsTable;
import sheet.management.api.SheetManager;
import utils.ServletUtils;

@WebServlet(name = "VersionUpdateServlet", urlPatterns = {"/sheetVersionUpdates"})
public class VersionUpdateServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String sheetName = request.getParameter("sheetName");
        String clientVersion = request.getParameter("currentVersion");

        // Validate parameters
        if (sheetName == null || clientVersion == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Invalid request");
            return;
        }

        SheetsTable sheetsTable = ServletUtils.getSheetsTable(getServletContext());
        SheetManager sheetManager = sheetsTable.getSheetManager(sheetName);
        int version = sheetManager.getCurrentSheet().getVersion();

        // Retrieve the current version from the server
        String latestVersion = String.valueOf(version);

        // Check if there's a new version
        if (!latestVersion.equals(clientVersion)) {
            response.getWriter().write("Update available");
        } else {
            response.getWriter().write("No update");
        }
    }
}
