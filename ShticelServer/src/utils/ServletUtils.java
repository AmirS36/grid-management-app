package utils;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import serverWide.chat.ChatManager;
import serverWide.sheetsTable.SheetsTable;
import serverWide.users.UserManager;

import static constants.Constants.INT_PARAMETER_ERROR;


public class ServletUtils {

    private static final String USER_MANAGER_ATTRIBUTE_NAME = "userManager";
    private static final String SHEET_TABLE_ATTRIBUTE_NAME = "sheetTable";
    private static final String CHAT_MANAGER_ATTRIBUTE_NAME = "chatManager";

    /*
    Note how the synchronization is done only on the question and\or creation of the relevant managers and once they exists -
    the actual fetch of them is remained un-synchronized for performance POV
     */

    private static final Object userManagerLock = new Object();
    private static final Object sheetTableLock = new Object();
    private static final Object chatManagerLock = new Object();


    public static UserManager getUserManager(ServletContext servletContext) {

        synchronized (userManagerLock) {
            if (servletContext.getAttribute(USER_MANAGER_ATTRIBUTE_NAME) == null) {
                servletContext.setAttribute(USER_MANAGER_ATTRIBUTE_NAME, new UserManager());
            }
        }
        return (UserManager) servletContext.getAttribute(USER_MANAGER_ATTRIBUTE_NAME);
    }

    public static SheetsTable getSheetsTable(ServletContext servletContext) {
        synchronized (sheetTableLock) {
            if (servletContext.getAttribute(SHEET_TABLE_ATTRIBUTE_NAME) == null) {
                servletContext.setAttribute(SHEET_TABLE_ATTRIBUTE_NAME, new SheetsTable());
            }
        }
        return (SheetsTable) servletContext.getAttribute(SHEET_TABLE_ATTRIBUTE_NAME);
    }

    public static ChatManager getChatManager(ServletContext servletContext) {
        synchronized (chatManagerLock) {
            if (servletContext.getAttribute(CHAT_MANAGER_ATTRIBUTE_NAME) == null) {
                servletContext.setAttribute(CHAT_MANAGER_ATTRIBUTE_NAME, new ChatManager());
            }
        }
        return (ChatManager) servletContext.getAttribute(CHAT_MANAGER_ATTRIBUTE_NAME);
    }

    public static int getIntParameter(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException numberFormatException) {
            }
        }
        return INT_PARAMETER_ERROR;
    }



}
