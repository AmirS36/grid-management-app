package servlets;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import management.SheetManagerConverter;
import management.SheetManagerDTO;
import serverWide.sheetsTable.SheetsTable;
import sheet.management.api.SheetManager;
import utils.ServletUtils;
import utils.SessionUtils;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "UpdateSheetCellServlet", urlPatterns = {"/updateSheetCell"})
public class UpdateSheetCellServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String sheetName = req.getParameter("sheetName");
        String cellID = req.getParameter("cellID");
        String value = req.getParameter("value");
        String type = req.getParameter("type");
        String usernameFromSession = SessionUtils.getUsername(req);

        // Input validation
        if (sheetName == null || cellID == null || value == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Missing parameters: sheetName, cellID, or value.");
            return;
        }

        try {
            // Retrieve the SheetManager from the context
            SheetsTable sheetsTable = ServletUtils.getSheetsTable(getServletContext());
            SheetManager sheetManager = sheetsTable.getSheetManager(sheetName);

            sheetManager.updateCell(cellID, value, usernameFromSession, type);



            // Send the updated SheetManagerDTO back to the client
            SheetManagerDTO sheetManagerDTO = SheetManagerConverter.toDTO(sheetManager);
            resp.setContentType("application/json");
            resp.setStatus(HttpServletResponse.SC_OK);

            PrintWriter out = resp.getWriter();
            out.print(new Gson().toJson(sheetManagerDTO));
            out.flush();

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("An error occurred: " + e.getMessage());
        }
    }
}
