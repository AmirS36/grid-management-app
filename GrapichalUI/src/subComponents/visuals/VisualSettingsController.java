package subComponents.visuals;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import subComponents.app.MainController;

public class VisualSettingsController {

    private MainController myMainController;

    @FXML private Button CenterAlignmentButton;
    @FXML private HBox alignmentButtons;
    @FXML private ColorPicker backgroundColorPicker;
    @FXML private TextField columnHeightTF;
    @FXML private Button leftAlignmentButton;
    @FXML private ColorPicker mainColorPicker;
    @FXML private Button rightAlignmentButton;
    @FXML private TextField rowWidthTF;

    public void setMainController(MainController myMainController) {
        this.myMainController = myMainController;
    }
}
