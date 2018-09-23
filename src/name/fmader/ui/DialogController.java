package name.fmader.ui;

import javafx.beans.property.BooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import name.fmader.datamodel.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class DialogController {

    private ToDoItem selectedToDoItem = null;

    private BooleanProperty okDisable;
    private String error = "";

    private DataIO dataIO = DataIO.getInstance();

    private ObservableList<ToDoItem> children = FXCollections.observableArrayList();
    private ObservableList<ToDoItem> parents = FXCollections.observableArrayList();
    private ObservableList<String> itemContexts = FXCollections.observableArrayList();
    private ObservableList<ToDoItem> toDoItems = FXCollections.observableList(dataIO.getToDoItems());
    private ObservableList<String> availableContexts = FXCollections.observableList(dataIO.getContexts());

    private FilteredList<ToDoItem> filteredDependencySource = new FilteredList<>(toDoItems);
    private FilteredList<String> filteredContextSource = new FilteredList<>(availableContexts);

    private List<ListView<ToDoItem>> listViews = new ArrayList<>();

    private Predicate<ToDoItem> excludeDependencies = toDoItem ->
            !children.contains(toDoItem) && !parents.contains(toDoItem) && !toDoItem.equals(selectedToDoItem);

    @FXML
    private Label typeLabel;
    @FXML
    private ChoiceBox<String> typeChoiceBox;
    @FXML
    private TextField titleTextField;
    @FXML
    private Label startLabel;
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
    private ListView<ToDoItem> childrenListView;
    @FXML
    private TextField filterDependencySourceTextField;
    @FXML
    private ListView<ToDoItem> dependencySourceListView;
    @FXML
    private ListView<ToDoItem> parentsListView;
    @FXML
    private ListView<String> contextsListView;
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
        timeLabel.setVisible(false);
        timeTextField.setVisible(false);
        neededLabel.setVisible(false);
        inheritedLabel.setVisible(false);
        fixCheckBox.setVisible(false);

        typeChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals("Appointment")) {
                deadlineLabel.setText("Date:");
                timeLabel.setVisible(true);
                timeTextField.setVisible(true);
                startLabel.setVisible(false);
                startDatePicker.setVisible(false);
            } else {
                deadlineLabel.setText("Deadline:");
                timeLabel.setVisible(false);
                timeTextField.setVisible(false);
                startLabel.setVisible(true);
                startDatePicker.setVisible(true);
            }
            if (newValue.equals("Project")) {
                recurrentCheckBox.setVisible(false);
                everyTextField.setVisible(false);
                recurringBaseChoiceBox.setVisible(false);
//                fixCheckBox.setVisible(false);
            } else {
                recurrentCheckBox.setVisible(true);
                everyTextField.setVisible(true);
                recurringBaseChoiceBox.setVisible(true);
//                fixCheckBox.setVisible(true);
            }
        });

        recurringBaseChoiceBox.getItems().add("days");
        recurringBaseChoiceBox.getItems().add("weeks");
        recurringBaseChoiceBox.getItems().add("months");
        recurringBaseChoiceBox.getItems().add("years");

        listViews.add(childrenListView);
        listViews.add(dependencySourceListView);
        listViews.add(parentsListView);

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

        childrenListView.setItems(children);
        parentsListView.setItems(parents);
        contextsListView.setItems(itemContexts);

        dependencySourceListView.setItems(filteredDependencySource);
        contextSourceListView.setItems(filteredContextSource);

        filterDependencySourceTextField.textProperty().addListener((observable, oldValue, newValue) ->
                filteredDependencySource.setPredicate(excludeDependencies.and(toDoItem -> {
                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }
                    return toDoItem.getTitle().toLowerCase().contains(newValue.toLowerCase());
                })));

        ListChangeListener<ToDoItem> setSourcePredicate = c ->
                filteredDependencySource.setPredicate(excludeDependencies.and(toDoItem -> {
                    String filter = filterDependencySourceTextField.getText();
                    if (filter == null || filter.isEmpty()) {
                        return true;
                    }
                    return toDoItem.getTitle().toLowerCase().contains(filter.toLowerCase());
                }));

        children.addListener(setSourcePredicate);
        parents.addListener(setSourcePredicate);

        itemContexts.addListener((ListChangeListener<String>) c ->
                filteredContextSource.setPredicate(string -> !itemContexts.contains(string)));
    }

    public void setOkDisable (BooleanProperty okDisable) {
        this.okDisable = okDisable;
        okDisable.set(!validInput());
        typeChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                okDisable.set(!validInput()));
        titleTextField.textProperty().addListener((observable, oldValue, newValue) ->
                okDisable.set(!validInput()));
        deadlineDatePicker.valueProperty().addListener((observable, oldValue, newValue) ->
                okDisable.set(!validInput()));
        timeTextField.textProperty().addListener((observable, oldValue, newValue) ->
                okDisable.set(!validInput()));
    }

    public void initForm(ToDoItem toDoItem) {
        selectedToDoItem = toDoItem;
        filteredDependencySource.setPredicate(excludeDependencies);

        String type = toDoItem.getClass().getSimpleName();

        if (!type.equals("ToDoItem")) {
            typeLabel.setText(type);
            typeChoiceBox.setValue(type);
        } else {
            typeLabel.setText("ToDo");
            typeChoiceBox.setValue("ToDo");
        }
        typeChoiceBox.setVisible(false);

        titleTextField.setText(toDoItem.getTitle());

        LocalDate start = toDoItem.getStart();
        if (start != null) {
            startDatePicker.setValue(start);
        }

        LocalDate deadline = toDoItem.getOriginalDeadline();
        if (deadline != null) {
            deadlineDatePicker.setValue(deadline);
        }

        if (type.equals("Appointment")) {
            timeTextField.setText(toDoItem.getDateTime().toLocalTime().toString());
        }

        if (type.equals("Appointment") || type.equals("External")) {
            if (toDoItem.isInherited()) {
                neededLabel.setVisible(true);
                inheritedLabel.setVisible(true);
                if (type.equals("Appointment")) {
                    neededLabel.setText("Needed date:");
                } else {
                    neededLabel.setText("Req. deadline:");
                }
                inheritedLabel.setText(toDoItem.getInheritedDeadline().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            } else {
                neededLabel.setVisible(false);
                inheritedLabel.setVisible(false);
            }
        } else {
            neededLabel.setVisible(true);
            neededLabel.setText("Inh. deadline:");
            if (toDoItem.isInherited()) {
                inheritedLabel.setVisible(true);
                inheritedLabel.setText(toDoItem.getDeadline().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            } else {
                neededLabel.setVisible(false);
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

        children.addAll(toDoItem.getChildren());
        parents.addAll(toDoItem.getParents());
        itemContexts.addAll(toDoItem.getContexts());

        String description = toDoItem.getDescription();
        if (description != null) {
            descriptionTextArea.setText(description);
        }
    }

    public ToDoItem getToDoItem() {
        ToDoItem newToDoItem;
        String type = typeChoiceBox.getValue();
        String title = titleTextField.getText().trim();
        LocalDate deadline = deadlineDatePicker.getValue();
        String timeString = timeTextField.getText();

        if (selectedToDoItem == null) {
            switch (type) {
                case "ToDo":
                    newToDoItem = new ToDoItem(title);
                    break;
                case "External":
                    newToDoItem = new External(title);
                    break;
                case "Appointment":
                    newToDoItem = new Appointment(title, deadline, LocalTime.parse(timeString, DateTimeFormatter.ofPattern("HH:mm")));
                    break;
                case "Project":
                    newToDoItem = new Project(title);
                    break;
                default:
                    newToDoItem = new ToDoItem(title);
            }
        } else {
            newToDoItem = selectedToDoItem;
            newToDoItem.setTitle(title);
        }

        newToDoItem.setStart(startDatePicker.getValue());

        if (!type.equals("Appointment") || selectedToDoItem != null) {
            newToDoItem.setDeadline(deadline);
        }

        if (type.equals("Appointment") && selectedToDoItem != null) {
            newToDoItem.setDateTime(deadline.atTime(LocalTime.parse(timeString, DateTimeFormatter.ofPattern("HH:mm"))));
        }

        newToDoItem.setRecurrent(recurrentCheckBox.isSelected());
        if (recurrentCheckBox.isSelected()) {
            String base = recurringBaseChoiceBox.getValue();
            RecurringBase recurringBase;
            switch (base) {
                case "days":
                    recurringBase = RecurringBase.EVERYNDAYS;
                    break;
                case "weeks":
                    recurringBase = RecurringBase.EVERYNWEEKS;
                    break;
                case "months":
                    recurringBase = RecurringBase.EVERYNMONTHS;
                    break;
                case "years":
                    recurringBase = RecurringBase.EVERYNYEARS;
                    break;
                default:
                    recurringBase = RecurringBase.EVERYNDAYS;
            }
            newToDoItem.setRecurringPattern(new RecurringPattern(fixCheckBox.isSelected(), recurringBase,
                    Integer.parseInt(everyTextField.getText())));
        } else {
            newToDoItem.setRecurringPattern(null);
        }

        List<ToDoItem> oldChildren = new ArrayList<>(newToDoItem.getChildren());
        for (ToDoItem toDoItem : oldChildren) {
            if (!children.contains(toDoItem)) {
                newToDoItem.removeChild(toDoItem);
            }
        }
        for (ToDoItem toDoItem : children) {
            newToDoItem.addChild(toDoItem);
        }

        List<ToDoItem> oldParents = new ArrayList<>(newToDoItem.getParents());
        for (ToDoItem toDoItem : oldParents) {
            if (!parents.contains(toDoItem)) {
                toDoItem.removeChild(newToDoItem);
            }
        }
        for (ToDoItem toDoItem : parents) {
            toDoItem.addChild(newToDoItem);
        }

        List<String> oldContexts = new ArrayList<>(newToDoItem.getContexts());
        for (String context : oldContexts) {
            if (!itemContexts.contains(context)) {
                newToDoItem.removeContext(context);
            }
        }
        for (String context : itemContexts) {
            newToDoItem.addContext(context);
        }

        String description = descriptionTextArea.getText();
        if (description != null && !description.trim().isEmpty()) {
            newToDoItem.setDescription(description.trim());
        }

        return newToDoItem;
    }

    @FXML
    private void addToChildren() {
        ToDoItem toDoItem = dependencySourceListView.getSelectionModel().getSelectedItem();
        if (toDoItem != null) {
            children.add(toDoItem);
        }
    }

    @FXML
    private void removeChild(KeyEvent event) {
        KeyCode keyCode = null;
        if (event != null) {
            keyCode = event.getCode();
        }
        if (event == null || keyCode.equals(KeyCode.DELETE) || keyCode.equals(KeyCode.BACK_SPACE)) {
            ToDoItem toDoItem = childrenListView.getSelectionModel().getSelectedItem();
            if (toDoItem != null) {
                children.remove(toDoItem);
            }
        }
    }

    @FXML
    private void handleChildrenMouseEvent(MouseEvent event) {
        if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
            removeChild(null);
        }
    }

    @FXML
    private void addToParents() {
        ToDoItem toDoItem = dependencySourceListView.getSelectionModel().getSelectedItem();
        if (toDoItem != null) {
            parents.add(toDoItem);
        }
    }

    @FXML
    private void removeParent(KeyEvent event) {
        KeyCode keyCode = null;
        if (event != null) {
            keyCode = event.getCode();
        }
        if (event == null || keyCode.equals(KeyCode.DELETE) || keyCode.equals(KeyCode.BACK_SPACE)) {
            ToDoItem toDoItem = parentsListView.getSelectionModel().getSelectedItem();
            if (toDoItem != null) {
                parents.remove(toDoItem);
            }
        }
    }

    @FXML
    private void handleParentsMouseEvent(MouseEvent event) {
        if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
            removeParent(null);
        }
    }

    @FXML
    private void addItemContext() {
        String context = contextSourceListView.getSelectionModel().getSelectedItem();
        if (context != null) {
            itemContexts.add(context);
        }
    }

    @FXML
    private void handleContextSourceMouseEvent(MouseEvent event) {
        if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
            addItemContext();
        }
    }

    @FXML
    private void removeItemContext(KeyEvent event) {
        KeyCode keyCode = null;
        if (event != null) {
            keyCode = event.getCode();
        }
        if (event == null || keyCode.equals(KeyCode.DELETE) || keyCode.equals(KeyCode.BACK_SPACE)) {
            String context = contextsListView.getSelectionModel().getSelectedItem();
            if (context != null) {
                itemContexts.remove(context);
            }
        }
    }

    @FXML
    private void handleItemContextsMouseEvent(MouseEvent event) {
        if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
            removeItemContext(null);
        }
    }

    @FXML
    private void addContext() {
        String context = newContextTextField.getText();
        if (context != null && !context.trim().isEmpty() && !availableContexts.contains(context.trim())) {
            availableContexts.add(context.trim());
            newContextTextField.clear();
        }
    }

    @FXML
    private void removeContext(KeyEvent event) {
        KeyCode keyCode = event.getCode();
        if (keyCode.equals(KeyCode.DELETE) || keyCode.equals(KeyCode.BACK_SPACE)) {
            String context = contextSourceListView.getSelectionModel().getSelectedItem();
            if (context != null) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle(null);
                alert.setHeaderText("Are you sure?");
                alert.setContentText("When deleting a context from the context source pane, it will be removed from all items as well.");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.CANCEL) {
                    return;
                }

                availableContexts.remove(context);
                for (ToDoItem toDoItem : toDoItems) {
                    toDoItem.getContexts().remove(context);
                }
            }
        }
    }

    private boolean validInput () {
        error = "";
        if (titleTextField.getText().isEmpty()) {
            error = "Title can't be empty.\n";
        }
        if (typeChoiceBox.getValue().equals("Appointment")) {
            if (deadlineDatePicker.getValue() == null) {
                error += "Date is required for appointments\n";
            }
            if (timeTextField == null || !timeTextField.getText().matches("^([0-1][0-9]|2[0-3]):[0-5][0-9]$")) {
                error += "Time (format hh:mm) is required for appointments\n";
            }
        }
        return error.isEmpty();
    }
}
