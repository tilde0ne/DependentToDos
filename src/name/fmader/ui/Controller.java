package name.fmader.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;
import name.fmader.datamodel.Appointment;
import name.fmader.datamodel.DataIO;
import name.fmader.datamodel.External;
import name.fmader.datamodel.ToDoItem;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class Controller {

    private DataIO dataIO = DataIO.getInstance();
    private ObservableList<ToDoItem> toDoItems;
    private ObservableList<String> contexts;

    private FilteredList<ToDoItem> filteredActiveToDoItems;
    private FilteredList<ToDoItem> filteredDependentToDoItems;
    private FilteredList<ToDoItem> filteredExternals;
    private FilteredList<ToDoItem> filteredAppointments;

    private ToDoItem selectedToDoItem;

    private Predicate<ToDoItem> isDoable = ToDoItem::isDoable;
    private Predicate<ToDoItem> isExternal = toDoItem -> toDoItem.getClass().getSimpleName().equals("External");
    private Predicate<ToDoItem> isAppointment = toDoItem -> toDoItem.getClass().getSimpleName().equals("Appointment");
    private Predicate<ToDoItem> isToDoItem = toDoItem -> toDoItem.getClass().getSimpleName().equals("ToDoItem");
    private Predicate<ToDoItem> isProject = toDoItem -> toDoItem.getClass().getSimpleName().equals("Project");
    private Predicate<ToDoItem> isToDoOrProject = isToDoItem.or(isProject);

    private Predicate<ToDoItem> activeToDoItemsPredicate = isToDoOrProject.and(isDoable);
    private Predicate<ToDoItem> dependentToDoItemsPredicate = isToDoOrProject.and(isDoable.negate());

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
    GridPane mainGridPane;
    @FXML
    GridPane detailPane;

    @FXML
    TableView<ToDoItem> activeToDoTableView;
    @FXML
    TableView<ToDoItem> dependentToDoTableView;
    @FXML
    TableView<ToDoItem> externalTableView;
    @FXML
    TableView<ToDoItem> appointmentTableView;

    List<TableView<ToDoItem>> tableViews = new ArrayList<>();

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
    Button addButton;
    @FXML
    Button editButton;

    public void initialize() {
        dataIO.load();
        toDoItems = FXCollections.observableArrayList(dataIO.getToDoItems());
        contexts = FXCollections.observableArrayList(dataIO.getContexts());

        filteredActiveToDoItems = new FilteredList<>(toDoItems, activeToDoItemsPredicate);
        filteredDependentToDoItems = new FilteredList<>(toDoItems, dependentToDoItemsPredicate);
        filteredExternals = new FilteredList<>(toDoItems, isExternal);
        filteredAppointments = new FilteredList<>(toDoItems, isAppointment);
        FilteredList<ToDoItem> projects = new FilteredList<>(toDoItems, isProject);

        SortedList<ToDoItem> activeToDoItems = new SortedList<>(filteredActiveToDoItems, sortByDeadline);
        SortedList<ToDoItem> dependentToDoItems = new SortedList<>(filteredDependentToDoItems, sortByDeadline);
        SortedList<ToDoItem> externals = new SortedList<>(filteredExternals, sortByIsDoable.thenComparing(sortByDeadline));
        SortedList<ToDoItem> appointments = new SortedList<>(filteredAppointments, sortByDateTime);

        activeToDoTableView.setItems(activeToDoItems);
        dependentToDoTableView.setItems(dependentToDoItems);
        externalTableView.setItems(externals);
        appointmentTableView.setItems(appointments);

        tableViews.add(activeToDoTableView);
        tableViews.add(dependentToDoTableView);
        tableViews.add(externalTableView);
        tableViews.add(appointmentTableView);

        for (TableView<ToDoItem> tableView : tableViews) {
            tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    for (TableView<ToDoItem> innerTableView : tableViews) {
                        if (innerTableView != tableView) {
                            innerTableView.getSelectionModel().clearSelection();
                        }
                    }
                    selectedToDoItem = tableView.getSelectionModel().getSelectedItem();
                    showDetails();
                } else {
                    selectNull();
                }
            });
        }

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

    @FXML
    public void clearProjectFilter() {
        projectChoiceBox.getSelectionModel().clearSelection();
    }

    @FXML
    public void clearContextFilter() {
        contextChoiceBox.getSelectionModel().clearSelection();
    }

    @FXML
    public void filterItems() {
        Predicate<ToDoItem> temp1 =
                toDoItem -> projectChoiceBox.getValue() == null || toDoItem.getDependedOnBy().contains(projectChoiceBox.getValue());
        Predicate<ToDoItem> temp2 =
                toDoItem -> contextChoiceBox.getValue() == null || toDoItem.getContexts().contains(contextChoiceBox.getValue());

        filteredActiveToDoItems.setPredicate(temp1.and(temp2).and(activeToDoItemsPredicate));
        filteredDependentToDoItems.setPredicate(temp1.and(temp2).and(dependentToDoItemsPredicate));
        filteredExternals.setPredicate(temp1.and(temp2).and(isExternal));
        filteredAppointments.setPredicate(temp1.and(temp2).and(isAppointment));
    }

    @FXML
    public void addOrEditToDoItem(ActionEvent event) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainGridPane.getScene().getWindow());

        if (event.getSource().equals(addButton)) {
            dialog.setTitle("Add new item");
        } else if (event.getSource().equals(editButton)) {
            if (selectedToDoItem == null) {
                alertNoSelection();
                return;
            }
            dialog.setTitle("Edit item");
        }

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("dialog.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        DialogController dialogController = fxmlLoader.getController();
        if (event.getSource().equals(editButton)) {
            dialogController.initForm(selectedToDoItem);
        }

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get().equals(ButtonType.OK)) {
            ToDoItem toDoItem = dialogController.getToDoItem();

            if (event.getSource().equals(addButton)) {
                toDoItems.add(toDoItem);
            } else if (event.getSource().equals(editButton)) {
                toDoItems.remove(selectedToDoItem);
                toDoItems.add(toDoItem);
            }

//            selectedToDoItem = toDoItem;
            for (TableView<ToDoItem> tableView : tableViews) {
                if (tableView.getItems().contains(toDoItem)) {
                    tableView.getSelectionModel().select(toDoItem);
                    showDetails();
                }
            }
        }
    }

    private void alertNoSelection() {

    }

    private void showDetails() {
        if (selectedToDoItem != null) {
            detailPane.setVisible(true);
        }

    }

    private void selectNull() {
        ToDoItem temp = null;
        for (TableView<ToDoItem> tableView : tableViews) {
            ToDoItem inspect = tableView.getSelectionModel().getSelectedItem();
            if (inspect != null) {
                temp = inspect;
            }
        }
        if (temp == null) {
            selectedToDoItem = null;
        }
        detailPane.setVisible(false);
    }
}
