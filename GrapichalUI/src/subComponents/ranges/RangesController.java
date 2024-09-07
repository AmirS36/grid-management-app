package subComponents.ranges;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import subComponents.app.MainController;

public class RangesController {

    @FXML private Button deleteRangeButton;
    @FXML private Button editRangeButton;
    @FXML private Button newRangeButton;
    @FXML private ScrollPane rangesScrollPane;

    private MainController myMainController;


    public void setMyMainController(MainController myMainController) {
        this.myMainController = myMainController;
    }
}
