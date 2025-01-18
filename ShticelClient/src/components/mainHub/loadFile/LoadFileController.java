package components.mainHub.loadFile;

import components.chatArea.ChatAreaController;
import components.gridArea.base.MainController;
import components.gridArea.grid.GridController;
import components.mainHub.base.MainHubController;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.*;
import logic.permissions.PermissionLevel;
import logic.tasks.LoadFileTask;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import util.Constants;
import util.http.HttpClientUtil;
//import logic.tasks.LoadFileTask;
//import subComponents.app.MainController;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import util.http.HttpClientUtil;

public class LoadFileController {

    private MainHubController myMainController;

    @FXML private Button loadFileButton;
    @FXML private Label loadFileTA;

    private SimpleStringProperty selectedFileProperty;


    public LoadFileController() {
        selectedFileProperty = new SimpleStringProperty();
    }

    @FXML
    private void initialize() {
        loadFileTA.textProperty().bind(selectedFileProperty);
    }

    public void setMainController(MainHubController myMainController) {
        this.myMainController = myMainController;
    }

    @FXML
    void loadFileButtonAction(ActionEvent event) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open XML File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML Files", "*.xml"));
        File selectedFile = fileChooser.showOpenDialog(myMainController.getPrimaryStage());

        if (selectedFile != null) {

            onCompletion();
            sendFileToServer(selectedFile);


        } else {
            selectedFileProperty.set("No File Selected");
        }
    }

    private void sendFileToServer(File selectedFile) {
        RequestBody body =
                new MultipartBody.Builder()
                        .addFormDataPart("sheetData", selectedFile.getName(), RequestBody.create(selectedFile, MediaType.parse("text/plain")))
                        .build();

        String finalUrl = HttpUrl
                        .parse(Constants.UPLOAD_PAGE)
                        .newBuilder()
                        .build()
                        .toString();

        Request request = new Request.Builder()
                .url(finalUrl)
                .post(body)
                .build();


        HttpClientUtil.runAsyncRequest(request, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->  myMainController.showError(new IOException("Failed to upload file")));

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                System.out.println("Response received, status code: " + response.code());

                if (response.code() != 200) {
                    String responseBody = response.body().string();
                    System.out.println("Error response from server: " + responseBody);
                    Platform.runLater(() -> {
                        myMainController.showError(new IOException("Something went wrong: " + responseBody));
                    });
                } else {
                    String responseBody = response.body().string();
                    Platform.runLater(() -> {
                        selectedFileProperty.set(responseBody);
                    });
                }
            }
        });

    }

    public void onCompletion() {
        loadFileTA.setVisible(true);
    }

    public void chatRoomButtonAction(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader();

        URL chatFXML = getClass().getResource("/components/chatArea/chat-area.fxml");
        loader.setLocation(chatFXML);
        Parent root = loader.load();

        // Get the controller and set necessary references
        ChatAreaController chatAreaController = loader.getController();
        chatAreaController.setMyMainController(myMainController);
        myMainController.setChatAreaController(chatAreaController);

        // Create a new stage for the chat room
        Stage chatStage = new Stage();
        chatStage.setTitle("Chat Room");

        Scene scene = new Scene(root);
        chatStage.setScene(scene);
        chatStage.setResizable(false);

        // Set the chat stage to be non-modal and non-blocking
        chatStage.initModality(Modality.NONE); // Allows interaction with main window

        // Optionally, make it a utility window style
        chatStage.initStyle(StageStyle.UTILITY); // Minimizes the window decoration

        // Position the chat stage to be centered on the primary stage
        Stage primaryStage = myMainController.getPrimaryStage();
        chatStage.setX(primaryStage.getX() + (primaryStage.getWidth() - chatStage.getWidth()) / 2);
        chatStage.setY(primaryStage.getY() + (primaryStage.getHeight() - chatStage.getHeight()) / 2);

        chatStage.show();
    }
}
