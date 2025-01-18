package logic.tasks;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import components.mainHub.base.MainHubController;
import components.mainHub.loadFile.LoadFileController;


import java.io.File;


public class LoadFileTask extends Task<Boolean> {
    private final File file;
    private final MainHubController mainHubController;
    private final LoadFileController loadFileController;


    public LoadFileTask(File file, MainHubController mainController, LoadFileController loadFileController) {
        this.file = file;
        this.mainHubController = mainController;
        this.loadFileController = loadFileController;
    }

    @Override
    protected Boolean call() {
        try {
            updateMessage("Loading file: " + file.getName() + "...");

            int totalSteps = 100;
            for (int i = 0; i < totalSteps; i++) {
                Thread.sleep(20);
                updateProgress(i + 1, totalSteps);
            }
            Platform.runLater(() -> {
                updateMessage("File: " + file.getName() + " loaded successfully");
            });
        }
        catch (Exception e) {
            Platform.runLater(() -> mainHubController.showError(e));
        }
        return true;
    }

    @Override
    protected void succeeded() {
        super.succeeded();
        loadFileController.onCompletion();
    }

    @Override
    protected void failed() {
        super.failed();
        Platform.runLater(() -> {
            new Alert(AlertType.ERROR, "Error loading file: " + getException().getMessage()).show();
        });
    }

    @Override
    protected void cancelled() {
        super.cancelled();
        // Hide the progress indicator and show a warning on the JavaFX Application Thread
        Platform.runLater(() -> {
            new Alert(AlertType.WARNING, "File loading cancelled").show();
        });
    }
}