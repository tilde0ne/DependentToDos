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
        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
        primaryStage.setTitle("Dependent ToDo's");
        primaryStage.setScene(new Scene(root, 1200, 900));
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        DataIO dataIO = DataIO.getInstance();
        dataIO.saveSettings();
        dataIO.save();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
