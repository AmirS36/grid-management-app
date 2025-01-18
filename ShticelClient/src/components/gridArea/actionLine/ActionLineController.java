package components.gridArea.actionLine;

import cell.CellDTO;
import components.gridArea.base.MainController;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.FillTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class ActionLineController {

    private MainController myMainController;

    @FXML private Label selectedCellTA;
    @FXML private TextField originalValueTF;
    @FXML private Button updateValueButton;
    @FXML private Label lastUpdateCellVersionTA;
    @FXML private Label gridVersionTA;
    @FXML private Button versionSelectorButton;
    @FXML private Button newVersionButton;

    private SimpleBooleanProperty isCellPresent;
    private SimpleBooleanProperty isGridPresent;

    public ActionLineController() {
        isCellPresent = new SimpleBooleanProperty(false);
        isGridPresent = new SimpleBooleanProperty(false);
    }

    @FXML
    private void initialize() {
        versionSelectorButton.disableProperty().bind(isGridPresent.not());
    }

    public void setPermissions(SimpleBooleanProperty isReader, SimpleBooleanProperty isWriter, SimpleBooleanProperty isOwner) {
        originalValueTF.disableProperty().bind(
                isCellPresent.not().or(isOwner.not().and(isWriter.not()))
        );
        updateValueButton.disableProperty().bind(
                isCellPresent.not().or(isOwner.not().and(isWriter.not()))
        );
    }

    public void setMainController(MainController myMainController) {
        this.myMainController = myMainController;
    }

    public void setIsCellPresent(boolean isCellPresent) {
        this.isCellPresent.set(isCellPresent);
    }

    public void setIsGreedPresent(boolean isGridPresent) {
        this.isGridPresent.set(isGridPresent);
    }

    public void updateActionLine(CellDTO cell) {
        if (cell == MainController.theEmptyCell || cell == null)
        {
            selectedCellTA.setText(" ");
            originalValueTF.setText(" ");
            lastUpdateCellVersionTA.setText(" ");
            gridVersionTA.setText(String.valueOf(myMainController.getCurrentSheet().getVersion()));
            return;
        }
        selectedCellTA.setText(cell.getCoordinate().toString());
        originalValueTF.setText(cell.getOriginalValue());
        lastUpdateCellVersionTA.setText(cell.getVersion() + " by " + cell.getLastUpdatedBy());
        gridVersionTA.setText(String.valueOf(myMainController.getCurrentSheet().getVersion()));
    }

    @FXML
    public void handleUpdateValueButton() {
        if (!myMainController.isLatestVersion()) {
            myMainController.showLoadingError(new IllegalStateException("New version available! Please update to the latest sheet version " +
                    "(through the button on the right of the action line) to enable editing."));
            return;
        }
        String coordinate = selectedCellTA.getText();
        String newValue = originalValueTF.getText();
        String originalValue;

        CellDTO cellFromLabel = myMainController.getGridComponentController().getCellFromLabel(myMainController.getGridComponentController().getSelectedCell());
        if (cellFromLabel == null) {
            originalValue = "";
        }
        else {
            originalValue = cellFromLabel.getOriginalValue();
        }
        if (originalValue.equals(newValue)) {
            return;
        }

        myMainController.updateCell(coordinate, newValue);
    }

    public void versionSelectorButtonAction(ActionEvent actionEvent) {
        myMainController.getGridComponentController().unclickCell();
        myMainController.showVersionSelectorDialog();
    }


    public SimpleBooleanProperty getIsCellPresent() {
        return  isCellPresent;
    }

    public String getSelectedCell() {
        return selectedCellTA.getText();
    }

    public void showUpdateNotification() {
        versionSelectorButton.setVisible(false);
        newVersionButton.setVisible(true);
        createBlinkingAnimation(newVersionButton);
    }

    public void createBlinkingAnimation(Button button) {
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1), button);
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.0);
        fadeTransition.setCycleCount(Animation.INDEFINITE);
        fadeTransition.setAutoReverse(true); // Make it blink back and forth
        fadeTransition.play();
    }

    public void newVersionButtonAction(ActionEvent actionEvent) {
        myMainController.getUpdatedSheet();
        versionSelectorButton.setVisible(true);
        newVersionButton.setVisible(false);
        myMainController.setIsLatestVersion(true);
    }
}
