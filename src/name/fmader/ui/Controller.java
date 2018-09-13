package name.fmader.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import name.fmader.datamodel.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.function.Predicate;

public class Controller {

    private DataIO dataIO = DataIO.getInstance();
    private ObservableList<ToDoItem> toDoItems;
    private ObservableList<String> contexts;

    private Predicate<ToDoItem> isDoable = toDoItem -> toDoItem.isDoable();
    private Predicate<ToDoItem> isExternal = toDoItem -> toDoItem.getClass().getSimpleName().equals("External");
    private Predicate<ToDoItem> isAppointment = toDoItem -> toDoItem.getClass().getSimpleName().equals("Appointment");
    private Predicate<ToDoItem> isToDoItem = toDoItem -> toDoItem.getClass().getSimpleName().equals("ToDoItem");
    private Predicate<ToDoItem> isProject = toDoItem -> toDoItem.getClass().getSimpleName().equals("Project");
    private Predicate<ToDoItem> isToDoOrProject = isToDoItem.or(isProject);

    private Comparator<ToDoItem> sortByDeadline = (o1, o2) -> {
        if (o1.getDeadline() == null) {
            if (o2.getDeadline() == null) {
                return 0;
            }
            return 1;
        }
        if (o2.getDeadline() == null) {
            return -1;
        }
        if (o1.getDeadline().equals(o2.getDeadline())) {
            return 0;
        }
        return o1.getDeadline().isAfter(o2.getDeadline()) ? 1 : -1;
    };

    private Comparator<ToDoItem> sortByDateTime = (o1, o2) -> {
        Appointment appointment1 = (Appointment) o1;
        Appointment appointment2 = (Appointment) o2;

        if (appointment1.getDateTime() == null) {
            if (appointment2.getDateTime() == null) {
                return 0;
            }
            return 1;
        }
        if (appointment2.getDateTime() == null) {
            return -1;
        }
        if (appointment1.getDateTime().equals(appointment2.getDateTime())) {
            return 0;
        }
        return appointment1.getDateTime().isAfter(appointment2.getDateTime()) ? 1 : -1;
    };

    private Comparator<ToDoItem> sortByIsDoable = (o1, o2) -> {
        if ((o1.isDoable() && o2.isDoable()) || (!o1.isDoable() && !o2.isDoable())) {
            return 0;
        }
        return o1.isDoable() ? 1 : -1;
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
    ChoiceBox<ToDoItem> projectChoiceBox;
    @FXML
    ChoiceBox<String> contextChoiceBox;
    @FXML
    CheckBox bothCheckBox;
    @FXML
    Button clearButton;

    public void initialize() {
        dataIO.load();
        toDoItems = FXCollections.observableArrayList(dataIO.getToDoItems());
        contexts = FXCollections.observableArrayList(dataIO.getContexts());

        FilteredList<ToDoItem> filteredActiveToDoItems = new FilteredList<>(toDoItems, isToDoOrProject.and(isDoable));
        FilteredList<ToDoItem> filteredDependentToDoItems = new FilteredList<>(toDoItems, isToDoOrProject.and(isDoable.negate()));
        FilteredList<ToDoItem> filteredExternals = new FilteredList<>(toDoItems, isExternal);
        FilteredList<ToDoItem> filteredAppointments = new FilteredList<>(toDoItems, isAppointment);
        FilteredList<ToDoItem> projects = new FilteredList<>(toDoItems, isProject);

        SortedList<ToDoItem> activeToDoItems = new SortedList<>(filteredActiveToDoItems, sortByDeadline);
        SortedList<ToDoItem> dependentToDoItems = new SortedList<>(filteredDependentToDoItems, sortByDeadline);
        SortedList<ToDoItem> externals = new SortedList<>(filteredExternals, sortByIsDoable.thenComparing(sortByDeadline));
        SortedList<ToDoItem> appointments = new SortedList<>(filteredAppointments, sortByDateTime);

        activeToDoTableView.setItems(activeToDoItems);
        dependentToDoTableView.setItems(dependentToDoItems);
        externalTableView.setItems(externals);
        appointmentTableView.setItems(appointments);

        activeTitleColumn.prefWidthProperty().bind(activeToDoTableView.widthProperty().subtract(activeDeadlineColumn.getWidth() + 2));
        dependentTitleColumn.prefWidthProperty().bind(dependentToDoTableView.widthProperty().subtract(dependentDeadlineColumn.getWidth() + 2));
        externalStringTableColumn.prefWidthProperty().bind(externalTableView.widthProperty().subtract(externalLocalDateTableColumn.getWidth() + 2));
        appointmentStringTableColumn.prefWidthProperty().bind(appointmentTableView.widthProperty().subtract(appointmentLocalDateTimeTableColumn.getWidth() + 2));

        projectChoiceBox.setItems(projects);
        projectChoiceBox.setConverter(new StringConverter<ToDoItem>() {
            @Override
            public String toString(ToDoItem object) {
                return object.getTitle();
            }

            @Override
            public ToDoItem fromString(String string) {
                return projectChoiceBox.getItems()
                        .stream()
                        .filter(toDoItem -> toDoItem.getTitle().equals(string))
                        .findFirst()
                        .orElse(null);
            }
        });
        contextChoiceBox.setItems(contexts);
    }
}
