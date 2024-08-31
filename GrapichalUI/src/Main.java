import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {

    private int clickCounter;
    private Button btn;

    @Override
    public void start(Stage primaryStage) {
        System.out.println("called on " + Thread.currentThread().getName());
        btn = new Button();
        btn.setText("Say 'Hello World'");
        doSomethingWithButton();

        StackPane root = new StackPane();
        root.getChildren().add(btn);

        Scene scene = new Scene(root, 300, 250);

        primaryStage.setTitle("Hello World!");
        primaryStage.setScene(scene);
        primaryStage.show();
        System.out.println("java fx is done");
    }

    private void doSomethingWithButton() {
        EventHandler<ActionEvent> actionEventEventHandler =
                event -> this.updateClickCounter();

        btn.setOnAction(actionEventEventHandler);
    }

    private void updateClickCounter(){
        ++clickCounter;
        btn.setText("Clicked " + clickCounter + " Times");
    }

    public static void main(String[] args) {
        new Thread(() -> System.out.println("blabla")).start();
        launch(args);
        System.out.println("main ended");

    }

}