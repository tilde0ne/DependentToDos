package name.fmader.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import name.fmader.datamodel.DataIO;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("main.fxml"));
        Parent root = fxmlLoader.load(); //FXMLLoader.load(getClass().getResource("main.fxml"));
        Controller controller = fxmlLoader.getController();
        controller.setStage(primaryStage);
        primaryStage.setTitle("Dependent ToDo's - " + DataIO.getInstance().getDataFile().getPath());
        primaryStage.setScene(new Scene(root, 1200, 900));
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        DataIO dataIO = DataIO.getInstance();
        dataIO.save();
        dataIO.saveSettings();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
