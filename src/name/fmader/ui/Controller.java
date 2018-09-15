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
import name.fmader.datamodel.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
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
    private GridPane mainGridPane;
    @FXML
    private GridPane detailPane;

    @FXML
    private TableView<ToDoItem> activeToDoTableView;
    @FXML
    private TableView<ToDoItem> dependentToDoTableView;
    @FXML
    private TableView<ToDoItem> externalTableView;
    @FXML
    private TableView<ToDoItem> appointmentTableView;

    private List<TableView<ToDoItem>> tableViews = new ArrayList<>();

    @FXML
    private TableColumn<ToDoItem, String> activeTitleColumn;
    @FXML
    private TableColumn<ToDoItem, LocalDate> activeDeadlineColumn;
    @FXML
    private TableColumn<ToDoItem, String> dependentTitleColumn;
    @FXML
    private TableColumn<ToDoItem, LocalDate> dependentDeadlineColumn;
    @FXML
    private TableColumn<ToDoItem, String> externalTitleColumn;
    @FXML
    private TableColumn<ToDoItem, LocalDate> externalDeadlineColumn;
    @FXML
    private TableColumn<ToDoItem, String> appointmentTitleColumn;
    @FXML
    private TableColumn<ToDoItem, LocalDateTime> appointmentDateTimeColumn;

    @FXML
    private ChoiceBox<ToDoItem> projectChoiceBox;
    @FXML
    private ChoiceBox<String> contextChoiceBox;

    @FXML
    private Button addButton;
    @FXML
    private Button editButton;

    @FXML
    private Label detailsType;
    @FXML
    private Label detailsTitle;
    @FXML
    private Label detailsStart;
    @FXML
    private Label detailsDeadlineLabel;
    @FXML
    private Label detailsDeadlineValue;
    @FXML
    private Label detailsNeeded;
    @FXML
    private Label detailsInherited;
    @FXML
    private Label detailsRecurrent;
    @FXML
    private Label detailsPattern;
    @FXML
    private ListView<ToDoItem> detailsChildren;
    @FXML
    private ListView<ToDoItem> detailsParents;
    @FXML
    private TextArea detailsDescription;

    public void initialize() {
        dataIO.load();
        toDoItems = FXCollections.observableList(dataIO.getToDoItems());
        contexts = FXCollections.observableArrayList(dataIO.getContexts());

        filteredActiveToDoItems = new FilteredList<>(toDoItems, activeToDoItemsPredicate);
        filteredDependentToDoItems = new FilteredList<>(toDoItems, dependentToDoItemsPredicate);
        filteredExternals = new FilteredList<>(toDoItems, isExternal);
        filteredAppointments = new FilteredList<>(toDoItems, isAppointment);
        FilteredList<ToDoItem> projects = new FilteredList<>(toDoItems, isProject);

        SortedList<ToDoItem> activeToDoItems = new SortedList<>(filteredActiveToDoItems, sortByDeadline);
        SortedList<ToDoItem> dependentToDoItems = new SortedList<>(filteredDependentToDoItems, sortByDeadline);
        SortedList<ToDoItem> externals = new SortedList<>(filteredExternals, sortByDeadline.thenComparing(sortByIsDoable));
        SortedList<ToDoItem> appointments = new SortedList<>(filteredAppointments, sortByDateTime.thenComparing(sortByIsDoable));

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
        externalTitleColumn.prefWidthProperty().bind(externalTableView.widthProperty().subtract(externalDeadlineColumn.getWidth() + 2));
        appointmentTitleColumn.prefWidthProperty().bind(appointmentTableView.widthProperty().subtract(appointmentDateTimeColumn.getWidth() + 2));

        activeDeadlineColumn.setCellFactory(param -> new DateCell());
        dependentDeadlineColumn.setCellFactory(param -> new DateCell());
        externalDeadlineColumn.setCellFactory(param -> new DateCell());
        appointmentDateTimeColumn.setCellFactory(param -> new DateTimeCell());

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

        detailsChildren.setCellFactory(lv -> new ListCell<ToDoItem>() {
            @Override
            protected void updateItem(ToDoItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(item.getTitle());
                }
            }
        });

        detailsParents.setCellFactory(lv -> new ListCell<ToDoItem>() {
            @Override
            protected void updateItem(ToDoItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(item.getTitle());
                }
            }
        });

        selectNull();
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
                toDoItem -> projectChoiceBox.getValue() == null || toDoItem.getParents().contains(projectChoiceBox.getValue());
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
        dialog.setResizable(true);

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
                // FilteredList's watch ObservableLists for Changes in the list
                // but not for changes of the lists members state.
                // If you add an item as a parent in dialog, the new parent
                // won't get properly filtered (i.e. stays in activeToDoTableView.
                // Workaround: Remove and then add again all former parents
                // and notParents of toDoItem to toDoItems.
                toDoItems.remove(toDoItem);
                toDoItems.add(toDoItem);
            }

            List<ToDoItem> oldChildren = dialogController.getOldChildren();
            List<ToDoItem> children = toDoItem.getChildren();
            List<ToDoItem> union = new ArrayList<>(children);
            union.addAll(oldChildren);
            List<ToDoItem> intersection = new ArrayList<>(children);
            intersection.retainAll(oldChildren);
            union.removeAll(intersection);
            toDoItems.removeAll(union);
            toDoItems.addAll(union);

            List<ToDoItem> oldParents = dialogController.getOldParents();
            List<ToDoItem> parents = toDoItem.getParents();
            union = new ArrayList<>(parents);
            union.addAll(oldParents);
            intersection = new ArrayList<>(parents);
            intersection.retainAll(oldParents);
            union.removeAll(intersection);
            toDoItems.removeAll(union);
            toDoItems.addAll(union);

            //////////////////////////////////////////////////////////////////////

            for (TableView<ToDoItem> tableView : tableViews) {
                if (tableView.getItems().contains(toDoItem)) {
                    tableView.getSelectionModel().select(toDoItem);
                    showDetails();
                }
            }
        }

        contexts.clear();
        contexts.addAll(dataIO.getContexts());
    }

    @FXML
    public void done() {
        if (selectedToDoItem == null) {
            alertNoSelection();
            return;
        }
        List<ToDoItem> children = new ArrayList<>(selectedToDoItem.getChildren());
        List<ToDoItem> parents = new ArrayList<>(selectedToDoItem.getParents());
        for (ToDoItem toDoItem : children) {
            selectedToDoItem.removeChild(toDoItem);
        }
        for (ToDoItem toDoItem : parents) {
            toDoItem.removeChild(selectedToDoItem);
        }
        toDoItems.remove(selectedToDoItem);
        selectNull();
    }

    private void alertNoSelection() {

    }

    private void showDetails() {
        if (selectedToDoItem == null) {
            detailPane.setVisible(false);
            return;
        }
        detailPane.setVisible(true);

        String type = selectedToDoItem.getClass().getSimpleName();
        if (type.equals("ToDoItem")) {
            type = "ToDo";
        }
        detailsType.setText(type);

        detailsTitle.setText(selectedToDoItem.getTitle());

        LocalDate start = selectedToDoItem.getStart();
        if (start != null) {
            detailsStart.setText(start.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        } else {
            detailsStart.setText("");
        }

        if (type.equals("Appointment")) {
            detailsDeadlineLabel.setText("Date/Time:");
            detailsDeadlineValue.setText(((Appointment) selectedToDoItem).getDateTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
        } else {
            detailsDeadlineLabel.setText("Deadline:");
            if (selectedToDoItem.getDeadline() != null) {
                detailsDeadlineValue.setText(selectedToDoItem.getDeadline().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            } else {
                detailsDeadlineValue.setText("");
            }
        }

        if ((type.equals("Appointment") || type.equals("External")) && selectedToDoItem.isInherited()) {
            detailsNeeded.setVisible(true);
            detailsInherited.setVisible(true);
            if (type.equals("Appointment")) {
                detailsInherited.setText(((Appointment) selectedToDoItem).getInheritedDeadline().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            }
            if (type.equals("External")) {
                detailsInherited.setText(((External) selectedToDoItem).getInheritedDeadline().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            }
        } else {
            detailsNeeded.setVisible(false);
            if (selectedToDoItem.isInherited()) {
                detailsInherited.setVisible(true);
                detailsInherited.setText("Is inherited!");
            } else {
                detailsInherited.setVisible(false);
            }
        }

        detailsRecurrent.setText(String.valueOf(selectedToDoItem.isRecurrent()));
        if (selectedToDoItem.isRecurrent()) {
            detailsPattern.setVisible(true);
            RecurringPattern pattern = selectedToDoItem.getRecurringPattern();
            String base;
            switch (pattern.getRecurringBase()) {
                case EVERYNDAYS:
                    base = "days";
                    break;
                case EVERYNWEEKS:
                    base = "weeks";
                    break;
                case EVERYNMONTHS:
                    base = "months";
                    break;
                case EVERYNYEARS:
                    base = "years";
                    break;
                default:
                    base = "days";
                    break;
            }
            detailsPattern.setText("Every " + pattern.getEveryN() + " " + base + (pattern.isFix()? ", fix" : ""));
        } else {
            detailsPattern.setVisible(false);
        }

        detailsChildren.setItems(FXCollections.observableArrayList(selectedToDoItem.getChildren()));
        detailsParents.setItems(FXCollections.observableArrayList(selectedToDoItem.getParents()));

        String description = selectedToDoItem.getDescription();
        if (description != null) {
            detailsDescription.setText(description);
        } else {
            detailsDescription.setText("");
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
