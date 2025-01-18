package servlets;

import com.google.gson.Gson;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import management.SheetManagerConverter;
import management.SheetManagerDTO;
import serverWide.sheetsTable.SheetsTable;
import sheet.SheetConverter;
import sheet.base.api.Sheet;
import sheet.SheetDTO;
import sheet.management.api.SheetManager;
import utils.ServletUtils;

import java.io.IOException;


@WebServlet(name = "GetSheetServlet", urlPatterns = {"/getSheet"})
public class GetSheetServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        String sheetName = request.getParameter("sheetName");


        SheetsTable sheetsTable = ServletUtils.getSheetsTable(getServletContext());
        SheetManager sheetManager = sheetsTable.getSheetManager(sheetName);

        if (sheetManager == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("{\"message\":\"Sheet not found\"}");
            return;
        }


        sheetManager.getCurrentSheet().updateAllValues();
        sheetManager.getVersionManager().saveSheetVersion(sheetManager.getCurrentSheet());
        SheetManagerDTO sheetManagerDTO = SheetManagerConverter.toDTO(sheetManager);


        response.getWriter().write(new Gson().toJson(sheetManagerDTO));
    }

}
