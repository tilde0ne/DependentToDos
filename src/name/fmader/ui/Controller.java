package name.fmader.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import name.fmader.datamodel.*;

import java.util.List;

public class Controller {

    private DataIO dataIO = DataIO.getInstance();
    private ObservableList<ToDoItem> toDoItems;
    private ObservableList<Appointment> appointments;
    private ObservableList<External> externals;
    private ObservableList<Project> projects;
    private ObservableList<String> contexts;

    @FXML
    TableView<ToDoItem> activeToDoTableView;
    @FXML
    TableView<ToDoItem> dependentToDoTableView;
    @FXML
    TableView<External> externalTableView;
    @FXML
    TableView<Appointment> appointmentTableView;
    @FXML
    TableView<Project> projectTableView;

    public void initialize() {
        dataIO.load();
        toDoItems = FXCollections.observableArrayList(dataIO.getToDoItems());
        appointments = FXCollections.observableArrayList(dataIO.getAppointments());
        externals = FXCollections.observableArrayList(dataIO.getExternals());
        projects = FXCollections.observableArrayList(dataIO.getProjects());
        contexts = FXCollections.observableArrayList(dataIO.getContexts());

        activeToDoTableView.setItems(toDoItems);
    }
}
