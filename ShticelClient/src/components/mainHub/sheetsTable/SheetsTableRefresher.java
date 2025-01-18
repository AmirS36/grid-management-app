package components.mainHub.sheetsTable;

import javafx.beans.property.BooleanProperty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import sheetsTable.SheetTableItem;
import sheetsTable.SheetTableItemDTO;
import util.Constants;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.TimerTask;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static util.Constants.GSON_INSTANCE;

public class SheetsTableRefresher extends TimerTask {

    private final Consumer<List<SheetTableItem>> sheetsListConsumer;
    private final BooleanProperty shouldUpdate;

    public SheetsTableRefresher(Consumer<List<SheetTableItem>> sheetsListConsumer, BooleanProperty shouldUpdate) {
        this.sheetsListConsumer = sheetsListConsumer;
        this.shouldUpdate = shouldUpdate;
    }

    @Override
    public void run() {
        if (!shouldUpdate.get()) {
            return;
        }

        HttpClientUtil.runAsync(Constants.SHEETS_TABLE, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseBody = response.body().string();

                // Check if the response is "Empty"
                if (responseBody.contains("\"message\":\"Empty\"")) {
                    // Handle the case when no sheets are returned
                    return;
                } else {
                    // Handle the case when sheets are returned
                    SheetTableItemDTO[] sheetArrayDTO = GSON_INSTANCE.fromJson(responseBody, SheetTableItemDTO[].class);

                    // Convert DTOs to SheetTableItem (with JavaFX properties)
                    List<SheetTableItem> sheetTableItems = Arrays.stream(sheetArrayDTO)
                            .map(dto -> new SheetTableItem(dto.getUploadedBy(), dto.getSheetName(),dto.getSheetSize(), dto.getPermissionLevel()))
                            .collect(Collectors.toList());

                    sheetsListConsumer.accept(sheetTableItems);
                }
            }

        });



    }
}
