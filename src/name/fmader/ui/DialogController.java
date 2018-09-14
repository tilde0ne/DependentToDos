package name.fmader.ui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import name.fmader.datamodel.ToDoItem;

public class DialogController {

    private boolean isNew = true;
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
    private Spinner<Integer> everySpinner;
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
        isNew = false;
        selectedToDoItem = toDoItem;
    }

    public ToDoItem getToDoItem() {
        return  null;
    }
}
