package name.fmader.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import name.fmader.datamodel.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Predicate;

public class Controller {

    private DataIO dataIO = DataIO.getInstance();
    private ObservableList<ToDoItem> toDoItems;
    private ObservableList<String> contexts;

    private Predicate<ToDoItem> isDoable = toDoItem -> toDoItem.isDoable();
    private Predicate<ToDoItem> isExternal = toDoItem -> toDoItem.getClass().getSimpleName().equals("External");
    private Predicate<ToDoItem> isAppointment = toDoItem -> toDoItem.getClass().getSimpleName().equals("Appointment");
    private Predicate<ToDoItem> isToDoOrProject = toDoItem -> {
        String itemClass = toDoItem.getClass().getSimpleName();
        return itemClass.equals("ToDoItem") || itemClass.equals("Project");
    };

    @FXML
    TableView<ToDoItem> activeToDoTableView;
    @FXML
    TableView<ToDoItem> dependentToDoTableView;
    @FXML
    TableView<ToDoItem> externalTableView;
    @FXML
    TableView<ToDoItem> appointmentTableView;
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
        contexts = FXCollections.observableArrayList(dataIO.getContexts());

        FilteredList<ToDoItem> activeToDoItems = new FilteredList<>(toDoItems, isToDoOrProject.and(isDoable));
        FilteredList<ToDoItem> dependentToDoItems = new FilteredList<>(toDoItems, isToDoOrProject.and(isDoable.negate()));
        FilteredList<ToDoItem> externals = new FilteredList<>(toDoItems, isExternal);
        FilteredList<ToDoItem> appointments = new FilteredList<>(toDoItems, isAppointment);

        activeToDoTableView.setItems(activeToDoItems);
        dependentToDoTableView.setItems(dependentToDoItems);
        externalTableView.setItems(externals);
        appointmentTableView.setItems(appointments);

        activeTitleColumn.prefWidthProperty().bind(activeToDoTableView.widthProperty().subtract(activeDeadlineColumn.getWidth() + 2));
        dependentTitleColumn.prefWidthProperty().bind(dependentToDoTableView.widthProperty().subtract(dependentDeadlineColumn.getWidth() + 2));
        externalStringTableColumn.prefWidthProperty().bind(externalTableView.widthProperty().subtract(externalLocalDateTableColumn.getWidth() + 2));
        appointmentStringTableColumn.prefWidthProperty().bind(appointmentTableView.widthProperty().subtract(appointmentLocalDateTimeTableColumn.getWidth() + 2));
    }
}
