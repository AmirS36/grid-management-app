package subComponents.loadFile;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import subComponents.main.mainController;

public class loadFileController {

    private mainController myMainController;

    @FXML private Button loadFileButton;
    @FXML private Label loadFileTA;

    public void setMainController(mainController myMainController) {
        this.myMainController = myMainController;
    }

}
