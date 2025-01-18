package components.gridArea.versionSelector.serverUpdates;

import components.gridArea.base.MainController;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import sheet.SheetDTO;
import util.Constants;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.util.TimerTask;


public class SheetUpdatesRefresher extends TimerTask {

    private final MainController myMainController;

    public SheetUpdatesRefresher(MainController myMainController) {
        this.myMainController = myMainController;
    }

    @Override
    public void run() {
        SheetDTO currentSheet = myMainController.getCurrentSheet();
        String sheetName = currentSheet.getName();
        String currentVersion = String.valueOf(currentSheet.getVersion());

        String finalUrl = HttpUrl
                .parse(Constants.SHEET_VERSION_UPDATES)
                .newBuilder()
                .addQueryParameter("sheetName", sheetName)
                .addQueryParameter("currentVersion", currentVersion)
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();

                    if ("Update available".equals(responseBody)) {
                        Platform.runLater(() -> showUpdateNotification());
                    }
                }
            }

        });
    }

    private void showUpdateNotification() {
        myMainController.showUpdateNotification();
        myMainController.setIsLatestVersion(false);
    }
}
