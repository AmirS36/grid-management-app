package subComponents.ranges;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import sheet.base.api.Sheet;
import sheet.base.impl.SheetImpl;
import sheet.ranges.Range;
import subComponents.app.MainController;

public class RangesController {

    private MainController myMainController;

    @FXML private Button deleteRangeButton;
    @FXML private Button editRangeButton;
    @FXML private Button newRangeButton;
    @FXML private ListView<Range> rangesListView;

    private SimpleBooleanProperty isRangeSelected;

    private ObservableList<Range> rangeList;

    public RangesController() {
        isRangeSelected = new SimpleBooleanProperty(false);
    }

    @FXML
    private void initialize() {
        deleteRangeButton.disableProperty().bind(isRangeSelected.not());
        editRangeButton.disableProperty().bind(isRangeSelected.not());
    }






    private void bindRanges() {
        // Initialize the observable list
        rangeList = FXCollections.observableArrayList();

        // Bind the ListView items to the observable list
        rangesListView.setItems(rangeList);

        // Set custom cell factory for displaying the ranges
        rangesListView.setCellFactory(new Callback<ListView<Range>, ListCell<Range>>() {
            @Override
            public ListCell<Range> call(ListView<Range> listView) {
                return new ListCell<Range>() {
                    @Override
                    protected void updateItem(Range range, boolean empty) {
                        super.updateItem(range, empty);
                        if (empty || range == null) {
                            setText(null);
                        } else {
                            setText(range.getName() + " (" + range.getCells().size() + " cells)");
                        }
                    }
                };
            }
        });

        // Setup ListView selection listener to enable/disable buttons
        rangesListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean isSelected = newSelection != null;
            isRangeSelected.set(isSelected);
        });

        // Call method to bind the ranges to the sheet
        setupRangeBindings();
    }

    private void setupRangeBindings() {
        // Bind the rangeList to the ranges in the sheet
        // Convert the Map<String, Range> to a List<Range>
        Sheet sheet = myMainController.getCurrentSheet();
        if (sheet != null) {
            rangeList.setAll(sheet.getRanges().values()); // Initial population with ranges

            // Add a listener to update rangeList when sheet's ranges change
            // Assuming that rangesProperty() returns an ObservableMap<String, Range>
            sheet.getRanges().addListener((MapChangeListener<String, Range>) change -> {
                // Handle map changes
                if (change.wasAdded()) {
                    rangeList.add(change.getValueAdded());
                } else if (change.wasRemoved()) {
                    rangeList.remove(change.getValueRemoved());
                }
            });
        }
    }

    public void setMyMainController(MainController myMainController) {
        this.myMainController = myMainController;
    }



}
