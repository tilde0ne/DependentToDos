package name.fmader.ui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import name.fmader.datamodel.Appointment;
import name.fmader.datamodel.External;
import name.fmader.datamodel.RecurringPattern;
import name.fmader.datamodel.ToDoItem;

import java.time.LocalDate;

public class DialogController {

    private ToDoItem selectedToDoItem = null;

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
    private ListView<ToDoItem> projectsListView;
    @FXML
    private Button addProjectButton;
    @FXML
    private TextField filterProjectSourceTextField;
    @FXML
    private ListView<ToDoItem> projectSourceListView;
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

    public void initForm(ToDoItem toDoItem) {
        selectedToDoItem = toDoItem;
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
            // implement set recurringBase
            if (recurringPattern.isFix()) {
                fixCheckBox.setSelected(true);
            }
        }
    }

    public ToDoItem getToDoItem() {
        return  null;
    }
}
