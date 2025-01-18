package servlets;


import cell.CellConverter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import serverWide.sheetsTable.SheetsTable;
import sheet.base.api.Sheet;
import sheet.cell.Cell;
import sheet.management.api.SheetManager;
import utils.ServletUtils;
import javafx.scene.paint.Color;


import java.io.IOException;

@WebServlet(name = "UpdateCellDesignServlet", urlPatterns = {"/updateCellDesign"})
public class UpdateCellDesignServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String sheetName = req.getParameter("sheetName");
        String cellID = req.getParameter("cellID");
        String updateType = req.getParameter("updateType");
        String colorHex = req.getParameter("colorHex");

        // Validate required parameters
        if (sheetName == null || cellID == null || updateType == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Missing required parameters.");
            return;
        }

        SheetsTable sheetsTable = ServletUtils.getSheetsTable(getServletContext());
        SheetManager sheetManager = sheetsTable.getSheetManager(sheetName);

        if (sheetManager == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write("Sheet not found.");
            return;
        }

        try {
            Sheet currentSheet = sheetManager.getCurrentSheet();
            Cell cell = currentSheet.getCell(cellID);

            if (cell == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("Cell not found.");
                return;
            }

            switch (updateType) {
                case "MainColor":
                    cell.setTextColor(Color.web(colorHex));
                    break;
                case "BackgroundColor":
                    cell.setBackgroundColor(Color.web(colorHex));
                    break;
                case "Clear":
                    cell.setDefaultColor();
                    break;
                default:
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().write("Invalid update type.");
                    return;
            }


            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write("Cell design updated successfully.");

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("An error occurred: " + e.getMessage());
        }
    }
}