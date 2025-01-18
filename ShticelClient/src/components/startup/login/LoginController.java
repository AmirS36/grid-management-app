package components.startup.login;


import components.mainHub.base.MainHubController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import util.Constants;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.net.URL;

public class LoginController {

    Stage primaryStage;

    @FXML private Button loginButton;
    @FXML private TextField usernameTF;
    @FXML private AnchorPane loginAnchorPane;
    @FXML private Label loginErrorLabel;

    private final StringProperty errorMessageProperty = new SimpleStringProperty();

    @FXML
    private void initialize() {
        loginErrorLabel.textProperty().bind(errorMessageProperty);
    }


    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    void loginButtonAction(ActionEvent event) {
        String userName = usernameTF.getText();
        if (userName.isEmpty()) {
            errorMessageProperty.set("User name is empty. \nYou can't login with empty user name");
            return;
        }

        String finalUrl = HttpUrl
                        .parse(Constants.LOGIN_PAGE)
                        .newBuilder()
                        .addQueryParameter("username", userName)
                        .build()
                        .toString();

        System.out.println("New request is launched for: " + finalUrl);

        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    loginErrorLabel.setVisible(true);
                    errorMessageProperty.set("Something went wrong: " + e.getMessage());
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    String responseBody = response.body().string();
                    Platform.runLater(() -> {
                        loginErrorLabel.setVisible(true);
                        errorMessageProperty.set("Something went wrong: " + responseBody);
                    });
                } else {
                    Platform.runLater(() -> {
                        try {
                            HttpClientUtil.setUserName(userName);
                            MainHubController mainHubController = initiateMainHub();
                            mainHubController.updateUserName(userName);

                        } catch (IOException e) {
                            errorMessageProperty.set("Something went wrong: " + e.getMessage());
                        }

                    });
                }
            }
        });


    }

    private MainHubController initiateMainHub() throws IOException {
        FXMLLoader loader = new FXMLLoader();

        URL loginFXML = getClass().getResource("/components/mainHub/base/home.fxml");
        loader.setLocation(loginFXML);
        Parent root = loader.load();

        MainHubController mainHubController = loader.getController();
        mainHubController.setPrimaryStage(primaryStage);

        Scene scene = new Scene(root);
        primaryStage.setTitle("Shticel - Main Hub");
        primaryStage.setScene(scene);

        primaryStage.setResizable(true);

        primaryStage.setX((Screen.getPrimary().getVisualBounds().getWidth() - primaryStage.getWidth()) / 2);
        primaryStage.setY((Screen.getPrimary().getVisualBounds().getHeight() - primaryStage.getHeight()) / 2);

        primaryStage.show();

        return mainHubController;
    }

}
