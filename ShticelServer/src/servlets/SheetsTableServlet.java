package servlets;

import com.google.gson.Gson;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import serverWide.permissions.PermissionManager;
import serverWide.sheetsTable.SheetsTable;
import sheet.base.api.Sheet;
import sheetsTable.SheetTableItem;
import sheetsTable.SheetTableItemDTO;
import utils.ServletUtils;
import utils.SessionUtils;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet(name = "SheetsTableServlet", urlPatterns = {"/sheetsTable"})
public class SheetsTableServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Returning JSON objects, not HTML
        response.setContentType("application/json");

        SheetsTable sheetsTable = ServletUtils.getSheetsTable(getServletContext());
        PermissionManager permissionManager = sheetsTable.getPermissionManager();
        String username = SessionUtils.getUsername(request);


        // Check if there are any sheets to return
        if (sheetsTable.getSheetCount() == 0) {
            // Return only the "Empty" message if there are no sheets
            response.getWriter().write("{\"message\":\"Empty\"}");
        } else {
            // Otherwise, return the JSON array of sheet data
            List<Sheet> allSheets = sheetsTable.getAllSheets();
            Gson gson = new Gson();

            // Map the sheets to SheetTableItem
            List<SheetTableItemDTO> sheetItems = allSheets.stream()
                    .map(sheet -> new SheetTableItemDTO(sheet.getOwner(), sheet.getName(), sheet.getSize(),
                            permissionManager.getPermission(sheet.getName(),username).toString()))
                    .collect(Collectors.toList());

            // Convert the list to JSON and write it to the response
            String json = gson.toJson(sheetItems);
            response.getWriter().write(json);
        }

        // Flush and close the writer to finalize the response
        response.getWriter().flush();
        response.getWriter().close();
    }
}
