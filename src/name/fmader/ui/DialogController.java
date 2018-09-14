package name.fmader.ui;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import name.fmader.datamodel.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class DialogController {

    private ToDoItem selectedToDoItem = null;

    private DataIO dataIO = DataIO.getInstance();

    private ObservableList<ToDoItem> children = FXCollections.observableArrayList();
    private ObservableList<ToDoItem> parents = FXCollections.observableArrayList();
    private ObservableList<String> itemContexts = FXCollections.observableArrayList();
    private ObservableList<ToDoItem> toDoItems = FXCollections.observableArrayList(dataIO.getToDoItems());
    private ObservableList<String> availableContexts = FXCollections.observableArrayList(dataIO.getContexts());

    private FilteredList<ToDoItem> filteredChildrenSource = new FilteredList<>(toDoItems);
    private FilteredList<ToDoItem> filteredParentSource = new FilteredList<>(toDoItems);
    private FilteredList<String> filteredContextSource = new FilteredList<>(availableContexts);

    private List<ListView<ToDoItem>> listViews = new ArrayList<>();

    private Predicate<ToDoItem> excludeSelected = toDoItem -> !toDoItem.equals(selectedToDoItem);
    private Predicate<ToDoItem> excludeChildren = toDoItem -> !children.contains(toDoItem);
    private Predicate<ToDoItem> excludeParents = toDoItem -> !parents.contains(toDoItem);
    private Predicate<String> excludeContexts = string -> !itemContexts.contains(string);

    @FXML
    private Label typeLabel;
    @FXML
    private ChoiceBox<String> typeChoiceBox;
    @FXML
    private TextField titleTextField;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private Label deadlineLabel;
    @FXML
    private DatePicker deadlineDatePicker;
    @FXML
    private Label timeLabel;
    @FXML
    private TextField timeTextField;
    @FXML
    private Label neededLabel;
    @FXML
    private Label inheritedLabel;
    @FXML
    private CheckBox recurrentCheckBox;
    @FXML
    private TextField everyTextField;
    @FXML
    private ChoiceBox<String> recurringBaseChoiceBox;
    @FXML
    private CheckBox fixCheckBox;
    @FXML
    private ListView<ToDoItem> dependenciesListView;
    @FXML
    private Button addDependencyButton;
    @FXML
    private TextField filterDepencySourceTextField;
    @FXML
    private ListView<ToDoItem> depencySourceListView;
    @FXML
    private ListView<ToDoItem> parentsListView;
    @FXML
    private Button addParentButton;
    @FXML
    private TextField filterParentSourceTextField;
    @FXML
    private ListView<ToDoItem> parentSourceListView;
    @FXML
    private ListView<String> contextsListView;
    @FXML
    private Button addContextButton;
    @FXML
    private Button newContextButton;
    @FXML
    private TextField newContextTextField;
    @FXML
    private ListView<String> contextSourceListView;
    @FXML
    private TextArea descriptionTextArea;

    public void initialize() {
        typeChoiceBox.getItems().add("ToDo");
        typeChoiceBox.getItems().add("External");
        typeChoiceBox.getItems().add("Appointment");
        typeChoiceBox.getItems().add("Project");

        typeChoiceBox.setValue("ToDo");
        titleTextField.setText("Untitled Item");
        timeLabel.setVisible(false);
        timeTextField.setVisible(false);
        neededLabel.setVisible(false);
        inheritedLabel.setVisible(false);

        recurringBaseChoiceBox.getItems().add("days");
        recurringBaseChoiceBox.getItems().add("weeks");
        recurringBaseChoiceBox.getItems().add("months");
        recurringBaseChoiceBox.getItems().add("years");

        listViews.add(dependenciesListView);
        listViews.add(depencySourceListView);
        listViews.add(parentsListView);
        listViews.add(parentSourceListView);

        for (ListView<ToDoItem> listView : listViews) {
            listView.setCellFactory(lv -> new ListCell<ToDoItem>() {
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
        }

        dependenciesListView.setItems(children);
        parentsListView.setItems(parents);
        contextsListView.setItems(itemContexts);

        depencySourceListView.setItems(filteredChildrenSource);
        parentSourceListView.setItems(filteredParentSource);
        contextSourceListView.setItems(filteredContextSource);

        filterDepencySourceTextField.textProperty().addListener(((observable, oldValue, newValue) ->
                filteredChildrenSource.setPredicate(excludeSelected.and(excludeChildren).and(toDoItem -> {
                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }
                    return toDoItem.getTitle().toLowerCase().contains(newValue.toLowerCase());
                }))));

        filterParentSourceTextField.textProperty().addListener(((observable, oldValue, newValue) ->
                filteredParentSource.setPredicate(excludeSelected.and(excludeParents).and(toDoItem -> {
                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }
                    return toDoItem.getTitle().toLowerCase().contains(newValue.toLowerCase());
                }))));

        children.addListener((ListChangeListener<ToDoItem>) c ->
                filteredChildrenSource.setPredicate(excludeSelected.and(excludeChildren).and(toDoItem -> {
                    String filter = filterDepencySourceTextField.getText();
                    if (filter == null || filter.isEmpty()) {
                        return true;
                    }
                    return toDoItem.getTitle().toLowerCase().contains(filter.toLowerCase());
                })));

        parents.addListener((ListChangeListener<ToDoItem>) c ->
                filteredParentSource.setPredicate(excludeSelected.and(excludeParents).and(toDoItem -> {
                    String filter = filterParentSourceTextField.getText();
                    if (filter == null || filter.isEmpty()) {
                        return true;
                    }
                    return toDoItem.getTitle().toLowerCase().contains(filter.toLowerCase());
                })));

        itemContexts.addListener((ListChangeListener<String>) c -> filteredContextSource.setPredicate(excludeContexts));
    }

    public void initForm(ToDoItem toDoItem) {
        selectedToDoItem = toDoItem;
        filteredChildrenSource.setPredicate(excludeSelected);
        filteredParentSource.setPredicate(excludeSelected);

        String type = toDoItem.getClass().getSimpleName();

        if (!type.equals("ToDoItem")) {
            typeLabel.setText(type);
        } else {
            typeLabel.setText("ToDo");
        }
        typeChoiceBox.setVisible(false);

        titleTextField.setText(toDoItem.getTitle());

        LocalDate start = toDoItem.getStart();
        if (start != null) {
            startDatePicker.setValue(start);
        }

        LocalDate deadline = toDoItem.getDeadline();
        if (deadline != null) {
            deadlineDatePicker.setValue(deadline);
        }

        if (type.equals("Appointment")) {
            deadlineLabel.setText("Date:");
            timeLabel.setVisible(true);
            timeTextField.setVisible(true);

            Appointment appointment = (Appointment) toDoItem;
            timeTextField.setText(appointment.getDateTime().toLocalTime().toString());
        } else {
            deadlineLabel.setText("Deadline:");
            timeLabel.setVisible(false);
            timeTextField.setVisible(false);
        }

        if (type.equals("Appointment") || type.equals("External")) {
            if (toDoItem.isInherited()) {
                neededLabel.setVisible(true);
                if (type.equals("Appointment")) {
                    Appointment appointment = (Appointment) toDoItem;
                    inheritedLabel.setText(appointment.getInheritedDeadline().toString());
                }
                if (type.equals("External")) {
                    External external = (External) toDoItem;
                    inheritedLabel.setText(external.getInheritedDeadline().toString());
                }
            } else {
                neededLabel.setVisible(false);
                inheritedLabel.setVisible(false);
            }
        } else {
            neededLabel.setVisible(false);
            if (toDoItem.isInherited()) {
                inheritedLabel.setVisible(true);
                inheritedLabel.setText("Deadline is inherited!");
            } else {
                inheritedLabel.setVisible(false);
            }
        }

        if (toDoItem.isRecurrent()) {
            recurrentCheckBox.setSelected(true);
            RecurringPattern recurringPattern = toDoItem.getRecurringPattern();
            everyTextField.setText(((Integer) recurringPattern.getEveryN()).toString());
            switch (recurringPattern.getRecurringBase()) {
                case EVERYNDAYS:
                    recurringBaseChoiceBox.setValue("days");
                    break;
                case EVERYNWEEKS:
                    recurringBaseChoiceBox.setValue("weeks");
                    break;
                case EVERYNMONTHS:
                    recurringBaseChoiceBox.setValue("months");
                    break;
                case EVERYNYEARS:
                    recurringBaseChoiceBox.setValue("years");
                    break;
                default:
                    recurringBaseChoiceBox.setValue("days");
            }
            if (recurringPattern.isFix()) {
                fixCheckBox.setSelected(true);
            }
        }

        for (ToDoItem childItem : toDoItem.getDependsOn()) {
            children.add(childItem);
        }

        for (ToDoItem parentItem : toDoItem.getDependedOnBy()) {
            parents.add(parentItem);
        }

        for (String context : toDoItem.getContexts()) {
            itemContexts.add(context);
        }

        String description = toDoItem.getDescription();
        if (description != null) {
            descriptionTextArea.setText(description);
        }
    }

    public ToDoItem getToDoItem() {
        return null;
    }
}
