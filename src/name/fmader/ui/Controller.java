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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import name.fmader.datamodel.*;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Predicate;

public class Controller {

    private Stage stage = null;

    private DataIO dataIO = DataIO.getInstance();
    private ObservableList<ToDoItem> toDoItemsBase;
    private ObservableList<String> contexts;

    private FilteredList<ToDoItem> filteredActiveToDoItems;
    private FilteredList<ToDoItem> filteredDependentToDoItems;
    private FilteredList<ToDoItem> filteredExternals;
    private FilteredList<ToDoItem> filteredAppointments;

    private ToDoItem selectedToDoItem;

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
        if (o1.getDateTime() == null) {
            if (o2.getDateTime() == null) {
                return 0;
            }
            return 1;
        }
        if (o2.getDateTime() == null) {
            return -1;
        }
        if (o1.getDateTime().equals(o2.getDateTime())) {
            return 0;
        }
        return o1.getDateTime().isAfter(o2.getDateTime()) ? 1 : -1;
    };

    private Comparator<ToDoItem> sortByDependent = (o1, o2) -> {
        if ((o1.getDependent() && o2.getDependent()) || (!o1.getDependent() && !o2.getDependent())) {
            return 0;
        }
        return o1.getDependent() ? -1 : 1;
    };

    private Comparator<ToDoItem> sortByFuture = (o1, o2) -> {
        boolean o1Future = o1.getStart() != null && o1.getStart().isAfter(LocalDate.now());
        boolean o2Future = o2.getStart() != null && o2.getStart().isAfter(LocalDate.now());
        if (o1Future && !o2Future) {
            return 1;
        }
        if (o2Future && !o1Future) {
            return -1;
        }
        return 0;
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
    private ListView<String> detailsContexts;
    @FXML
    private TextArea detailsDescription;

    public void initialize() {
        dataIO.loadSettings();
        dataIO.load();
        toDoItemsBase = FXCollections.observableList(dataIO.getToDoItems(), item ->
                new Observable[]{item.titleProperty(), item.deadlineProperty(), item.dateTimeProperty(),
                        item.startProperty(), item.dependentProperty()});
        contexts = FXCollections.observableList(dataIO.getContexts());

        filteredActiveToDoItems = new FilteredList<>(toDoItemsBase, isToDoOrProject.and(item ->
                (item.getStart() == null || !item.getStart().isAfter(LocalDate.now())) && !item.getDependent()));
        filteredDependentToDoItems = new FilteredList<>(toDoItemsBase, isToDoOrProject.and(item ->
                (item.getStart() != null && item.getStart().isAfter(LocalDate.now())) || item.getDependent()));
        filteredExternals = new FilteredList<>(toDoItemsBase, isExternal);
        filteredAppointments = new FilteredList<>(toDoItemsBase, isAppointment);
        FilteredList<ToDoItem> projects = new FilteredList<>(toDoItemsBase, isProject);

        SortedList<ToDoItem> activeToDoItems = new SortedList<>(
                filteredActiveToDoItems, sortByDeadline.thenComparing(sortByTitle));
        SortedList<ToDoItem> dependentToDoItems = new SortedList<>(
                filteredDependentToDoItems, sortByFuture.thenComparing(sortByDeadline).thenComparing(sortByTitle));
        SortedList<ToDoItem> externals = new SortedList<>(
                filteredExternals, sortByFuture.thenComparing(sortByDeadline).thenComparing(sortByDependent).thenComparing(sortByTitle));
        SortedList<ToDoItem> appointments = new SortedList<>(
                filteredAppointments, sortByDateTime.thenComparing(sortByDependent).thenComparing(sortByTitle));

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

        PseudoClass dependent = PseudoClass.getPseudoClass("dependent");
        PseudoClass independent = PseudoClass.getPseudoClass("independent");
        PseudoClass future = PseudoClass.getPseudoClass("future");

        for (TableView<ToDoItem> tableView : tableViews) {
            tableView.setRowFactory(param -> {
                TableRow<ToDoItem> row = new TableRow<>();

                ChangeListener<Boolean> dependentListener = (observable, oldValue, newValue) -> {
                    row.pseudoClassStateChanged(dependent, newValue);
                    row.pseudoClassStateChanged(independent, !newValue);
                };

                ChangeListener<LocalDate> startListener = (observable, oldValue, newValue) ->
                        row.pseudoClassStateChanged(future, newValue != null && newValue.isAfter(LocalDate.now()));

                row.itemProperty().addListener((observable, oldValue, newValue) -> {
                    if (oldValue != null) {
                        oldValue.dependentProperty().removeListener(dependentListener);
                        oldValue.startProperty().removeListener(startListener);
                    }
                    if (newValue == null) {
                        row.pseudoClassStateChanged(dependent, false);
                        row.pseudoClassStateChanged(independent, false);
                        row.pseudoClassStateChanged(future, false);
                    } else {
                        row.pseudoClassStateChanged(dependent, newValue.getDependent());
                        row.pseudoClassStateChanged(independent, !newValue.getDependent());
                        row.pseudoClassStateChanged(future, newValue.getStart() != null && newValue.getStart().isAfter(LocalDate.now()));
                        newValue.dependentProperty().addListener(dependentListener);
                        newValue.startProperty().addListener(startListener);
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
    private void clearProjectFilter() {
        projectChoiceBox.getSelectionModel().clearSelection();
    }

    @FXML
    private void clearContextFilter() {
        contextChoiceBox.getSelectionModel().clearSelection();
    }

    @FXML
    private void filterItems() {
        Predicate<ToDoItem> projectFilter =
                toDoItem -> projectChoiceBox.getValue() == null || toDoItem.getParents().contains(projectChoiceBox.getValue());
        Predicate<ToDoItem> contextFilter =
                toDoItem -> contextChoiceBox.getValue() == null || toDoItem.getContexts().contains(contextChoiceBox.getValue());

        filteredActiveToDoItems.setPredicate(projectFilter.and(contextFilter).and(isToDoOrProject.and(item ->
                (item.getStart() == null || !item.getStart().isAfter(LocalDate.now())) && !item.getDependent())));
        filteredDependentToDoItems.setPredicate(projectFilter.and(contextFilter).and(isToDoOrProject.and(item ->
                (item.getStart() != null && item.getStart().isAfter(LocalDate.now())) || item.getDependent())));
        filteredExternals.setPredicate(projectFilter.and(contextFilter).and(isExternal));
        filteredAppointments.setPredicate(projectFilter.and(contextFilter).and(isAppointment));

        selectNull();
    }

    @FXML
    private void addOrEditToDoItem(ActionEvent event) {
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
        dialogController.setOkDisable(dialog.getDialogPane().lookupButton(ButtonType.OK).disableProperty());
        if (event.getSource().equals(editButton)) {
            dialogController.initForm(selectedToDoItem);
        }

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get().equals(ButtonType.OK)) {
            ToDoItem toDoItem = dialogController.getToDoItem();

            if (event.getSource().equals(addButton)) {
                toDoItemsBase.add(toDoItem);
            } else {
                filterItems();
            }

            for (TableView<ToDoItem> tableView : tableViews) {
                if (tableView.getItems().contains(toDoItem)) {
                    tableView.getSelectionModel().select(toDoItem);
                    showDetails();
                }
            }

            stage.setTitle("Dependent ToDo's - " + dataIO.getDataFile().getPath() + "*");
        }

        List<String> newContexts = dialogController.getAvailableContexts();
        List<String> oldContexts = new ArrayList<>(contexts);
        Collections.sort(newContexts);
        Collections.sort(oldContexts);
        if (!oldContexts.equals(newContexts)) {
            for (String context : newContexts) {
                if (!contexts.contains(context)) {
                    contexts.add(context);
                }
            }

            for (String context : oldContexts) {
                if (!newContexts.contains(context)) {
                    if (contextChoiceBox.getValue().equals(context)) {
                        clearContextFilter();
                    }
                    contexts.remove(context);
                }
            }

            stage.setTitle("Dependent ToDo's - " + dataIO.getDataFile().getPath() + "*");
        }
    }

    @FXML
    private void done() {
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

                    break;

                case "Appointment":
                    LocalTime time = itemToRemove.getDateTime().toLocalTime();
                    newToDoItem = new Appointment(title, newDeadline, time);
                    newToDoItem.setDescription(description);
                    newToDoItem.setRecurrent(true);
                    newToDoItem.setRecurringPattern(recurringPattern);

                    newToDoItem.setContexts(newContexts);
                    for (ToDoItem parent : newParents) {
                        parent.addChild(newToDoItem);
                    }

                    break;

                default:
                    newToDoItem = new ToDoItem("Something went wrong!");
                    newToDoItem.setDescription("Something went wrong, please mark this item done.");
            }

            toDoItemsBase.add(newToDoItem);
        }

        List<ToDoItem> children = new ArrayList<>(itemToRemove.getChildren());
        List<ToDoItem> parents = new ArrayList<>(itemToRemove.getParents());
        for (ToDoItem toDoItem : children) {
            itemToRemove.removeChild(toDoItem);
        }
        for (ToDoItem toDoItem : parents) {
            toDoItem.removeChild(itemToRemove);
        }
        toDoItemsBase.remove(itemToRemove);
        selectNull();
        stage.setTitle("Dependent ToDo's - " + dataIO.getDataFile().getPath() + "*");
    }

    @FXML
    private void newFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("New");
        fileChooser.setInitialDirectory(new File(dataIO.getSettings().getCustomPath()));
        FileChooser.ExtensionFilter dtdExtension = new FileChooser.ExtensionFilter("Dependent ToDo's Files", "*.dtd");
        fileChooser.getExtensionFilters().addAll(
                dtdExtension,
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        fileChooser.setSelectedExtensionFilter(dtdExtension);
        File file = fileChooser.showSaveDialog(mainGridPane.getScene().getWindow());
        if (file != null) {
            dataIO.save();
            dataIO.getSettings().setLastFile(file);
            dataIO.saveSettings();
            toDoItemsBase.clear();
            dataIO.getContexts().clear();
            dataIO.save(file);
            initialize();
            stage.setTitle("Dependent ToDo's - " + dataIO.getDataFile().getPath());
        }
    }

    @FXML
    private void open() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open");
        fileChooser.setInitialDirectory(new File(dataIO.getSettings().getCustomPath()));
        FileChooser.ExtensionFilter dtdExtension = new FileChooser.ExtensionFilter("Dependent ToDo's Files", "*.dtd");
        fileChooser.getExtensionFilters().addAll(
                dtdExtension,
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        fileChooser.setSelectedExtensionFilter(dtdExtension);
        File file = fileChooser.showOpenDialog(mainGridPane.getScene().getWindow());
        if (file != null) {
            dataIO.save();
            dataIO.getSettings().setLastFile(file);
            dataIO.saveSettings();
            initialize();
            stage.setTitle("Dependent ToDo's - " + dataIO.getDataFile().getPath());
        }
    }

    @FXML
    private void save() {
        dataIO.save();
        dataIO.saveSettings();
        stage.setTitle("Dependent ToDo's - " + dataIO.getDataFile().getPath());
    }

    @FXML
    private void saveAs() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save file as...");
        fileChooser.setInitialDirectory(new File(dataIO.getSettings().getCustomPath()));
        FileChooser.ExtensionFilter dtdExtension = new FileChooser.ExtensionFilter("Dependent ToDo's Files", "*.dtd");
        fileChooser.getExtensionFilters().addAll(
                dtdExtension,
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        fileChooser.setSelectedExtensionFilter(dtdExtension);
        File file = fileChooser.showSaveDialog(mainGridPane.getScene().getWindow());
        if (file != null) {
            dataIO.save(file);
            dataIO.saveSettings();
            stage.setTitle("Dependent ToDo's - " + dataIO.getDataFile().getPath());
        }
    }

    @FXML
    private void setDefaultDirectory() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Set default directory");
        directoryChooser.setInitialDirectory(new File(dataIO.getSettings().getCustomPath()));
        File file = directoryChooser.showDialog(mainGridPane.getScene().getWindow());
        if (file != null) {
            dataIO.getSettings().setCustomPath(file.getPath());
            dataIO.saveSettings();
        }
    }

    @FXML
    private void backup() {
        dataIO.backup();
    }

    @FXML
    private void handleTableViewMouseEvent(MouseEvent event) {
        if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
            addOrEditToDoItem(new ActionEvent(editButton, null));
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
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
            detailsDeadlineValue.setText(selectedToDoItem.getDateTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
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
            } else {
                detailsNeeded.setText("Req. Deadline:");
            }
            detailsInherited.setText(selectedToDoItem.getInheritedDeadline().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        } else {
            detailsNeeded.setVisible(false);
            if (selectedToDoItem.isInherited()) {
                detailsInherited.setVisible(true);
                detailsInherited.setText("Is inherited!");
            } else {
                detailsInherited.setVisible(false);
            }
        }

        if (selectedToDoItem.isRecurrent()) {
            detailsRecurrent.setVisible(true);
            detailsPattern.setVisible(true);
            RecurringPattern pattern = selectedToDoItem.getRecurringPattern();
            String base = null;
            switch (pattern.getRecurringBase()) {
                case EVERYNDAYS:
                    base = "day(s)";
                    break;
                case EVERYNWEEKS:
                    base = "week(s)";
                    break;
                case EVERYNMONTHS:
                    base = "month(s)";
                    break;
                case EVERYNYEARS:
                    base = "year(s)";
                    break;
            }
            detailsPattern.setText("Every " + pattern.getEveryN() + " " + base);
        } else {
            detailsRecurrent.setVisible(false);
            detailsPattern.setVisible(false);
        }

        detailsChildren.setItems(new SortedList<>(
                FXCollections.observableArrayList(selectedToDoItem.getChildren()), sortByTitle));
        detailsParents.setItems(new SortedList<>(
                FXCollections.observableArrayList(selectedToDoItem.getParents()), sortByTitle));
        detailsContexts.setItems(new SortedList<>(
                FXCollections.observableArrayList(selectedToDoItem.getContexts()), Comparator.naturalOrder()));

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
