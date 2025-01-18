package servlets;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import management.SheetManagerConverter;
import management.SheetManagerDTO;
import range.RangeConverter;
import range.RangeDTO;
import serverWide.sheetsTable.SheetsTable;
import sheet.base.api.Sheet;
import sheet.base.impl.SheetUtils;
import sheet.management.api.SheetManager;
import sheet.management.impl.SheetManagerImpl;
import sheet.ranges.Range;
import utils.ServletUtils;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "RangeActionServlet", urlPatterns = {"/rangeAction"})
public class RangeActionServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Parse parameters from the request body (form data or JSON)
        String action = req.getParameter("action");  // "add" or "delete"
        String sheetName = req.getParameter("sheetName");
        String rangeName = req.getParameter("rangeName");

        if (sheetName == null || rangeName == null) {
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
            switch (action) {
                case "add":
                    String fromCoord = req.getParameter("fromCoord");
                    String toCoord = req.getParameter("toCoord");

                    if (fromCoord == null || toCoord == null) {
                        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        resp.getWriter().write("Missing range coordinates.");
                        return;
                    }

                    // Add range logic
                    Sheet currentSheet = sheetManager.getCurrentSheet();
                    Range range = currentSheet.createRange(
                            rangeName,
                            SheetUtils.convertCellIdToCoordinate(fromCoord),
                            SheetUtils.convertCellIdToCoordinate(toCoord)
                    );
                    currentSheet.addRange(range);
                    RangeDTO rangeDTO = RangeConverter.toDTO(range);

                    resp.setContentType("application/json");
                    resp.getWriter().write(new Gson().toJson(rangeDTO));
                    break;

                case "delete":
                    // Delete range logic
                    try {
                        sheetManager.getCurrentSheet().removeRange(rangeName);
                        resp.setStatus(HttpServletResponse.SC_OK);
                    } catch (Exception e) {
                        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        resp.getWriter().write(e.getMessage());
                    }
                    break;

                default:
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().write("Invalid action.");
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("An error occurred: " + e.getMessage());
        }
    }
}
