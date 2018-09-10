package name.fmader.datamodel;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.time.LocalDate;
import java.util.List;

public class ToDoItem {

    private SimpleStringProperty title;
    private String description;

    private SimpleObjectProperty<LocalDate> deadline;
    private LocalDate start;

    private List<ToDoItem> dependsOn;
    private List<ToDoItem> dependedOnBy;
    private List<String> contexts;

    private boolean isRecurrent;
    private RecurringPattern recurringPattern;

    public ToDoItem(String title) {
        this.title = new SimpleStringProperty(title);
    }

    public String getTitle() {
        return title.get();
    }

    public SimpleStringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDeadline() {
        return (deadline == null) ? null : deadline.get();
    }

    public ObjectProperty<LocalDate> deadlineProperty() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        if (this.deadline != null) {
            this.deadline.set(deadline);
        } else {
            this.deadline = new SimpleObjectProperty<>(deadline);
        }
    }

    public LocalDate getStart() {
        return start;
    }

    public void setStart(LocalDate start) {
        this.start = start;
    }

    public List<ToDoItem> getDependsOn() {
        return dependsOn;
    }

    public List<ToDoItem> getDependedOnBy() {
        return dependedOnBy;
    }

    public List<String> getContexts() {
        return contexts;
    }

    public boolean isRecurrent() {
        return isRecurrent;
    }

    public void setRecurrent(boolean recurrent) {
        isRecurrent = recurrent;
    }

    public RecurringPattern getRecurringPattern() {
        return recurringPattern;
    }

    public void setRecurringPattern(RecurringPattern recurringPattern) {
        this.recurringPattern = recurringPattern;
    }
}
