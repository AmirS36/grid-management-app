package servlets;

import fileHandling.xml.XMLSheetLoader;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.io.InputStream;

import serverWide.permissions.PermissionManager;
import serverWide.permissions.PermissionType;
import serverWide.sheetsTable.SheetsTable;
import sheet.base.api.Sheet;
import sheet.management.api.SheetManager;
import sheet.management.impl.SheetManagerImpl;
import utils.ServletUtils;
import utils.SessionUtils;


@WebServlet(name = "UploadServlet", urlPatterns = {"/uploadSheet"})
@MultipartConfig
public class UploadServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain;charset=UTF-8");

        Part filePart = request.getPart("sheetData");
        InputStream fileInputStream = filePart.getInputStream();

        SheetsTable sheetsTable = ServletUtils.getSheetsTable(getServletContext());
        PermissionManager permissionManager = sheetsTable.getPermissionManager();
        String username = SessionUtils.getUsername(request);

        try {
            Sheet sheet = XMLSheetLoader.getSheetFromInputStream(fileInputStream);
            sheet.setOwner(username);
            validateSheet(sheet);

            String sheetName = sheet.getName();
            if (sheetsTable.sheetExists(sheetName)) { // You need to implement this method to check for existing sheets
                response.getWriter().write("Error: A sheet with the name '" + sheetName + "' already exists.");
                return;
            }

            // Save the sheet if everything is fine
            SheetManager sheetManager = new SheetManagerImpl(username, sheet);
            sheetsTable.addSheet(sheetName, sheetManager); // Implement this to save the sheet object
            permissionManager.setPermission(sheetName, username, PermissionType.OWNER);
            permissionManager.requestPermission(sheetName, username, PermissionType.OWNER);
            response.getWriter().write("Server says: Sheet '" + sheetName + "' was uploaded and saved successfully!");

        } catch (jakarta.xml.bind.JAXBException e) {
            response.getWriter().write("Error: Unable to parse sheet file. " + e.getMessage());
        } catch (Exception e) {
            response.getWriter().write("Error: " + e.getMessage());
        }
    }

    private void validateSheet(Sheet sheet) {
        if (sheet.getName() == null || sheet.getName().isEmpty()) {
            throw new IllegalStateException("Sheet name cannot be null or empty.");
        }
    }
}
