package util;

import com.google.gson.Gson;

public class Constants {

    // global constants
    public final static String LINE_SEPARATOR = System.getProperty("line.separator");
    public final static String JHON_DOE = "<Anonymous>";
    public final static int REFRESH_RATE = 2000;
    public final static String CHAT_LINE_FORMATTING = "%tH:%tM:%tS | %.10s: %s%n";

    // fxml locations
    public final static String MAIN_PAGE_FXML_RESOURCE_LOCATION = "/chat/client/component/main/chat-app-main.fxml";
    public final static String LOGIN_PAGE_FXML_RESOURCE_LOCATION = "/chat/client/component/login/login.fxml";
    public final static String CHAT_ROOM_FXML_RESOURCE_LOCATION = "/chat/client/component/chatroom/chat-room-main.fxml";

    // Server resources locations
    public final static String BASE_DOMAIN = "localhost";
    private final static String BASE_URL = "http://" + BASE_DOMAIN + ":8080";
    private final static String CONTEXT_PATH = "/shticelApp";
    private final static String FULL_SERVER_PATH = BASE_URL + CONTEXT_PATH;

    public final static String LOGIN_PAGE = FULL_SERVER_PATH + "/loginShortResponse";
    public final static String UPLOAD_PAGE = FULL_SERVER_PATH + "/uploadSheet";
    public final static String SHEETS_TABLE = FULL_SERVER_PATH + "/sheetsTable";
    public final static String PERMISSIONS_TABLE = FULL_SERVER_PATH + "/getPermissions";
    public final static String REQUEST_PERMISSION = FULL_SERVER_PATH + "/requestPermission";
    public final static String APPROVE_DECLINE_PERMISSION = FULL_SERVER_PATH + "/approveDeclinePermission";
    public final static String GET_SHEET = FULL_SERVER_PATH + "/getSheet";
    public final static String UPDATE_SHEET_CELL = FULL_SERVER_PATH + "/updateSheetCell";
    public final static String UPDATE_CELL_DESIGN = FULL_SERVER_PATH + "/updateCellDesign";
    public final static String RANGE_ACTION = FULL_SERVER_PATH + "/rangeAction";
    public final static String SHEET_VERSION_UPDATES = FULL_SERVER_PATH + "/sheetVersionUpdates";
    public final static String SEND_CHAT_LINE = FULL_SERVER_PATH + "/pages/chatroom/sendChat";
    public final static String CHAT_LINES_LIST = FULL_SERVER_PATH + "/chat";


    // GSON instance
    public final static Gson GSON_INSTANCE = new Gson();
}
