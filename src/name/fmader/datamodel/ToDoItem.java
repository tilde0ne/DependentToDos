package name.fmader.datamodel;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public class ToDoItem {

    private SimpleStringProperty title;
    private String description;

    private ObjectProperty<LocalDate> deadline;
    private LocalDate start;

    private List<ToDoItem> dependsOn;
    private List<ToDoItem> dependedOnBy;
    private List<String> contexts;

    private boolean isRecurrent;
    private RecurringPattern recurringPattern;
}
