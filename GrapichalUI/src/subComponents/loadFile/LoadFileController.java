package subComponents.loadFile;

import fileHandling.xml.XMLSheetLoader;
import jakarta.xml.bind.JAXBException;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import subComponents.app.MainController;

import java.io.File;

public class LoadFileController {


    @FXML private Button loadFileButton;
    @FXML private Label loadFileTA;

    private MainController myMainController;

    private SimpleStringProperty selectedFileProperty;



    public LoadFileController() {
        selectedFileProperty = new SimpleStringProperty();
    }

    @FXML
    private void initialize() {
        loadFileTA.textProperty().bind(selectedFileProperty);
    }





    public void setMainController(MainController myMainController) {
        this.myMainController = myMainController;
    }

    @FXML
    void loadFileButtonAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open XML File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML Files", "*.xml"));
        File selectedFile = fileChooser.showOpenDialog(myMainController.getPrimaryStage());

        try {
            // You can now proceed to load and process the XML file
            selectedFileProperty.set("File selected: " + selectedFile.getAbsolutePath());
            // TODO - Change after schema update!!
            myMainController.setCurrentSheet(XMLSheetLoader.loadXMLFile(selectedFile.getAbsolutePath()));
            // Add code here to parse and process the XML

        } catch (JAXBException e) {
            selectedFileProperty.set("No File Selected");
        }

    }

}
