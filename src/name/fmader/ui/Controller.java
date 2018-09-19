package name.fmader.ui;

import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.css.PseudoClass;
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
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class Controller {

    private DataIO dataIO = DataIO.getInstance();
    private ObservableList<ToDoItem> toDoItemsData;
    private ObservableList<ToDoItem> toDoItemsBase;
    private ObservableList<Appointment> appointmentsBase;
    private ObservableList<String> contexts;

    private FilteredList<ToDoItem> filteredActiveToDoItems;
    private FilteredList<ToDoItem> filteredDependentToDoItems;
    private FilteredList<ToDoItem> filteredExternals;
    private FilteredList<Appointment> filteredAppointments;

    private ToDoItem selectedToDoItem;

    private Predicate<ToDoItem> isExternal = toDoItem -> toDoItem.getClass().getSimpleName().equals("External");
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
        if ((o1.getDoable() && o2.getDoable()) || (!o1.getDoable() && !o2.getDoable())) {
            return 0;
        }
        return o1.getDoable() ? 1 : -1;
    };

    private Comparator<ToDoItem> sortByTitle = Comparator.comparing(ToDoItem::getTitle);

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
        toDoItemsData = FXCollections.observableList(dataIO.getToDoItems());
        toDoItemsBase = FXCollections.observableArrayList(item ->
                new Observable[]{item.titleProperty(), item.deadlineProperty(), item.startProperty(), item.doableProperty()});
        toDoItemsBase.addAll(toDoItemsData);
        appointmentsBase = FXCollections.observableArrayList(item ->
                new Observable[]{item.titleProperty(), item.dateTimeProperty(), item.doableProperty()});
        for (ToDoItem toDoItem : toDoItemsData) {
            if (toDoItem.getClass().getSimpleName().equals("Appointment")) {
                appointmentsBase.add((Appointment) toDoItem);
            }
        }
        contexts = FXCollections.observableArrayList(dataIO.getContexts());

        filteredActiveToDoItems = new FilteredList<>(toDoItemsBase, isToDoOrProject.and(item ->
                (item.getStart() == null || !item.getStart().isAfter(LocalDate.now())) && item.getDoable()));
        filteredDependentToDoItems = new FilteredList<>(toDoItemsBase, isToDoOrProject.and(item ->
                (item.getStart() != null && item.getStart().isAfter(LocalDate.now())) || !item.getDoable()));
        filteredExternals = new FilteredList<>(toDoItemsBase, isExternal);
        filteredAppointments = new FilteredList<>(appointmentsBase, item -> true);
        FilteredList<ToDoItem> projects = new FilteredList<>(toDoItemsData, isProject);

        SortedList<ToDoItem> activeToDoItems = new SortedList<>(filteredActiveToDoItems, sortByDeadline.thenComparing(sortByTitle));
        SortedList<ToDoItem> dependentToDoItems = new SortedList<>(filteredDependentToDoItems, sortByDeadline.thenComparing(sortByTitle));
        SortedList<ToDoItem> externals = new SortedList<>(filteredExternals, sortByDeadline.thenComparing(sortByIsDoable).thenComparing(sortByTitle));
        SortedList<ToDoItem> appointments = new SortedList<>(filteredAppointments, sortByDateTime.thenComparing(sortByIsDoable).thenComparing(sortByTitle));

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
                }
            });
        }

        activeTitleColumn.prefWidthProperty().bind(activeToDoTableView.widthProperty().subtract(activeDeadlineColumn.getWidth() + 2));
        dependentTitleColumn.prefWidthProperty().bind(dependentToDoTableView.widthProperty().subtract(dependentDeadlineColumn.getWidth() + 2));
        externalTitleColumn.prefWidthProperty().bind(externalTableView.widthProperty().subtract(externalDeadlineColumn.getWidth() + 2));
        appointmentTitleColumn.prefWidthProperty().bind(appointmentTableView.widthProperty().subtract(appointmentDateTimeColumn.getWidth() + 2));

        activeTitleColumn.setCellFactory(param -> new TitleCell());
        dependentTitleColumn.setCellFactory(param -> new TitleCell());
        externalTitleColumn.setCellFactory(param -> new TitleCell());
        appointmentTitleColumn.setCellFactory(param -> new TitleCell());

        activeDeadlineColumn.setCellFactory(param -> new DateCell());
        dependentDeadlineColumn.setCellFactory(param -> new DateCell());
        externalDeadlineColumn.setCellFactory(param -> new DateCell());
        appointmentDateTimeColumn.setCellFactory(param -> new DateTimeCell());

        PseudoClass doable = PseudoClass.getPseudoClass("doable");
        PseudoClass notDoable = PseudoClass.getPseudoClass("notdoable");

        for (TableView<ToDoItem> tableView : tableViews) {
            tableView.setRowFactory(param -> {
                TableRow<ToDoItem> row = new TableRow<>();

                ChangeListener<Boolean> doableListener = (observable, oldValue, newValue) -> {
                    row.pseudoClassStateChanged(doable, newValue);
                    row.pseudoClassStateChanged(notDoable, !newValue);
                };

                row.itemProperty().addListener((observable, oldValue, newValue) -> {
                    if (oldValue != null) {
                        oldValue.doableProperty().removeListener(doableListener);
                    }
                    if (newValue == null) {
                        row.pseudoClassStateChanged(doable, false);
                        row.pseudoClassStateChanged(notDoable, false);
                    } else {
                        row.pseudoClassStateChanged(doable, newValue.getDoable());
                        row.pseudoClassStateChanged(notDoable, !newValue.getDoable());
                        newValue.doableProperty().addListener(doableListener);
                    }
                });
                return row;
            });
        }

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
                if (item == null || empty) {
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
                if (item == null || empty) {
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
        Predicate<ToDoItem> projectFilter =
                toDoItem -> projectChoiceBox.getValue() == null || toDoItem.getParents().contains(projectChoiceBox.getValue());
        Predicate<ToDoItem> contextFilter =
                toDoItem -> contextChoiceBox.getValue() == null || toDoItem.getContexts().contains(contextChoiceBox.getValue());

        filteredActiveToDoItems.setPredicate(projectFilter.and(contextFilter).and(isToDoOrProject.and(item ->
                (item.getStart() == null || !item.getStart().isAfter(LocalDate.now())) && item.getDoable())));
        filteredDependentToDoItems.setPredicate(projectFilter.and(contextFilter).and(isToDoOrProject.and(item ->
                (item.getStart() != null && item.getStart().isAfter(LocalDate.now())) || !item.getDoable())));
        filteredExternals.setPredicate(projectFilter.and(contextFilter).and(isExternal));
        filteredAppointments.setPredicate(projectFilter.and(contextFilter));

        selectNull();
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
                if (toDoItem.getClass().getSimpleName().equals("Appointment")) {
                    appointmentsBase.add((Appointment) toDoItem);
                } else {
                    toDoItemsBase.add(toDoItem);
                }
                toDoItemsData.add(toDoItem);
            }

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

        ToDoItem itemToRemove = selectedToDoItem;

        if (itemToRemove.isRecurrent()) {
            RecurringPattern recurringPattern = itemToRemove.getRecurringPattern();
            String title = itemToRemove.getTitle();
            String type = itemToRemove.getClass().getSimpleName();
            LocalDate deadline = itemToRemove.getOriginalDeadline();
            LocalDate start = itemToRemove.getStart();
            String description = itemToRemove.getDescription();
            List<String> newContexts = itemToRemove.getContexts();
            List<ToDoItem> newParents = new ArrayList<>();

            for (ToDoItem parent : itemToRemove.getParents()) {
                if (parent.getClass().getSimpleName().equals("Project")) {
                    newParents.add(parent);
                }
            }

            LocalDate newDeadline = null;
            LocalDate newStart = null;
            int everyN = recurringPattern.getEveryN();
            RecurringBase base = recurringPattern.getRecurringBase();
            boolean fix = recurringPattern.isFix();

            long offset = 0L;
            if (deadline != null && start != null) {
                offset = ChronoUnit.DAYS.between(start, deadline);
            }

            switch (base) {
                case EVERYNDAYS:
                    if (deadline != null) {
                        newDeadline = deadline.plusDays(everyN);
                        while (!newDeadline.isAfter(LocalDate.now())) {
                            newDeadline = newDeadline.plusDays(everyN);
                        }
                        if (start != null && newDeadline.minusDays(offset).isAfter(LocalDate.now())) {
                            newStart = newDeadline.minusDays(offset);
                        }
                    } else {
                            newStart = LocalDate.now().plusDays(everyN);
                    }
                    break;

                case EVERYNWEEKS:
                    if (deadline != null) {
                        newDeadline = deadline.plusWeeks(everyN);
                        while (!newDeadline.isAfter(LocalDate.now())) {
                            newDeadline = newDeadline.plusWeeks(everyN);
                        }
                        if (start != null && newDeadline.minusDays(offset).isAfter(LocalDate.now())) {
                            newStart = newDeadline.minusDays(offset);
                        }
                    } else {
                        newStart = LocalDate.now().plusWeeks(everyN);
                    }
                    break;

                case EVERYNMONTHS:
                    if (deadline != null) {
                        newDeadline = deadline.plusMonths(everyN);
                        while (!newDeadline.isAfter(LocalDate.now())) {
                            newDeadline = newDeadline.plusMonths(everyN);
                        }
                        if (start != null && newDeadline.minusDays(offset).isAfter(LocalDate.now())) {
                            newStart = newDeadline.minusDays(offset);
                        }
                    } else {
                        newStart = LocalDate.now().plusMonths(everyN);
                    }
                    break;

                case EVERYNYEARS:
                    if (deadline != null) {
                        newDeadline = deadline.plusYears(everyN);
                        while (!newDeadline.isAfter(LocalDate.now())) {
                            newDeadline = newDeadline.plusYears(everyN);
                        }
                        if (start != null && newDeadline.minusDays(offset).isAfter(LocalDate.now())) {
                            newStart = newDeadline.minusDays(offset);
                        }
                    } else {
                        newStart = LocalDate.now().plusYears(everyN);
                    }
                    break;
            }

            ToDoItem newToDoItem;
            switch (type) {
                case "ToDoItem":
                    newToDoItem = new ToDoItem(title);
                    newToDoItem.setDeadline(newDeadline);
                    newToDoItem.setStart(newStart);
                    newToDoItem.setDescription(description);
                    newToDoItem.setRecurrent(true);
                    newToDoItem.setRecurringPattern(recurringPattern);

                    newToDoItem.setContexts(newContexts);
                    for (ToDoItem parent : newParents) {
                        parent.addChild(newToDoItem);
                    }

                    toDoItemsBase.add(newToDoItem);
                    break;

                case "External":
                    newToDoItem = new External(title);
                    newToDoItem.setDeadline(newDeadline);
                    newToDoItem.setStart(newStart);
                    newToDoItem.setDescription(description);
                    newToDoItem.setRecurrent(true);
                    newToDoItem.setRecurringPattern(recurringPattern);

                    newToDoItem.setContexts(newContexts);
                    for (ToDoItem parent : newParents) {
                        parent.addChild(newToDoItem);
                    }

                    toDoItemsBase.add(newToDoItem);
                    break;

                case "Appointment":
                    LocalTime time = ((Appointment) itemToRemove).getDateTime().toLocalTime();
                    newToDoItem = new Appointment(title, newDeadline, time);
                    newToDoItem.setDescription(description);
                    newToDoItem.setRecurrent(true);
                    newToDoItem.setRecurringPattern(recurringPattern);

                    newToDoItem.setContexts(newContexts);
                    for (ToDoItem parent : newParents) {
                        parent.addChild(newToDoItem);
                    }

                    appointmentsBase.add((Appointment) newToDoItem);
                    break;

                    default:
                        newToDoItem = new ToDoItem("Something went wrong!");
                        newToDoItem.setDescription("Something went wrong, please mark this item done.");
                        toDoItemsBase.add(newToDoItem);
            }

            toDoItemsData.add(newToDoItem);
        }

        List<ToDoItem> children = new ArrayList<>(itemToRemove.getChildren());
        List<ToDoItem> parents = new ArrayList<>(itemToRemove.getParents());
        for (ToDoItem toDoItem : children) {
            itemToRemove.removeChild(toDoItem);
        }
        for (ToDoItem toDoItem : parents) {
            toDoItem.removeChild(itemToRemove);
        }
        toDoItemsData.remove(itemToRemove);
        if (itemToRemove.getClass().getSimpleName().equals("Appointment")) {
            appointmentsBase.remove(itemToRemove);
        } else {
            toDoItemsBase.remove(itemToRemove);
        }
        selectNull();
    }

    @FXML
    public void backup() {
        dataIO.backup();
    }

    private void alertNoSelection() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(null);
        alert.setHeaderText(null);
        alert.setContentText("No item selected!");
        alert.showAndWait();
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
                detailsNeeded.setText("Needed Date:");
                detailsInherited.setText(((Appointment) selectedToDoItem).getInheritedDeadline().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            }
            if (type.equals("External")) {
                detailsNeeded.setText("Req. Deadline:");
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
            detailsPattern.setText("Every " + pattern.getEveryN() + " " + base + (pattern.isFix() ? ", fix" : ""));
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
            detailPane.setVisible(false);
        } else {
            selectedToDoItem = temp;
            showDetails();
        }
    }
}
