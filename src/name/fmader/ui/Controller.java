package name.fmader.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import name.fmader.datamodel.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    @FXML
    TableColumn<ToDoItem, String> activeTitleColumn;
    @FXML
    TableColumn<ToDoItem, LocalDate> activeDeadlineColumn;
    @FXML
    TableColumn<ToDoItem, String> dependentTitleColumn;
    @FXML
    TableColumn<ToDoItem, LocalDate> dependentDeadlineColumn;
    @FXML
    TableColumn<External, String> externalStringTableColumn;
    @FXML
    TableColumn<External, LocalDate> externalLocalDateTableColumn;
    @FXML
    TableColumn<Appointment, String> appointmentStringTableColumn;
    @FXML
    TableColumn<Appointment, LocalDateTime> appointmentLocalDateTimeTableColumn;
    @FXML
    TableColumn<Project, String> projectStringTableColumn;
    @FXML
    TableColumn<Project, LocalDate> projectLocalDateTableColumn;

    public void initialize() {
        dataIO.load();
        toDoItems = FXCollections.observableArrayList(dataIO.getToDoItems());
        appointments = FXCollections.observableArrayList(dataIO.getAppointments());
        externals = FXCollections.observableArrayList(dataIO.getExternals());
        projects = FXCollections.observableArrayList(dataIO.getProjects());
        contexts = FXCollections.observableArrayList(dataIO.getContexts());

        activeToDoTableView.setItems(toDoItems);
        dependentToDoTableView.setItems(toDoItems);
        externalTableView.setItems(externals);
        appointmentTableView.setItems(appointments);
        projectTableView.setItems(projects);

        activeTitleColumn.prefWidthProperty().bind(activeToDoTableView.widthProperty().subtract(activeDeadlineColumn.getWidth()));
        dependentTitleColumn.prefWidthProperty().bind(dependentToDoTableView.widthProperty().subtract(dependentDeadlineColumn.getWidth()));
        externalStringTableColumn.prefWidthProperty().bind(externalTableView.widthProperty().subtract(externalLocalDateTableColumn.getWidth()));
        appointmentStringTableColumn.prefWidthProperty().bind(appointmentTableView.widthProperty().subtract(appointmentLocalDateTimeTableColumn.getWidth()));
        projectStringTableColumn.prefWidthProperty().bind(projectTableView.widthProperty().subtract(projectLocalDateTableColumn.getWidth()));
    }
}
