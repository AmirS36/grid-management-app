package subComponents.actionLine;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import subComponents.app.MainController;

public class ActionLineController {

    private MainController myMainController;

    @FXML private TextField originalValueTF;
    @FXML private Label selectedCellID;
    @FXML private Label selectedCellTA;
    @FXML private Button updateValueButton;
    @FXML private Button versionSelectorButton;

    public void setMainController(MainController myMainController) {
        this.myMainController = myMainController;
    }

}
