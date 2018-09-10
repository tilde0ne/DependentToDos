package name.fmader.datamodel;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.time.LocalDate;
import java.util.ArrayList;
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

    public void addDependsOn(ToDoItem toDoItem) {
        if (dependsOn == null) {
            dependsOn = new ArrayList<>();
        }
        if (!dependsOn.contains(toDoItem)) {
            dependsOn.add(toDoItem);
            toDoItem.addDependedOnBy(this);
        }
    }

    public void removeDependsOn(ToDoItem toDoItem) {
        if (dependsOn != null) {
            dependsOn.remove(toDoItem);
            toDoItem.removeDependedOnBy(this);
        }
    }

    public void addDependedOnBy(ToDoItem toDoItem) {
        if (dependedOnBy == null) {
            dependedOnBy = new ArrayList<>();
        }
        if (!dependedOnBy.contains(toDoItem)) {
            dependedOnBy.add(toDoItem);
        }
    }

    public void removeDependedOnBy(ToDoItem toDoItem) {
        if (dependedOnBy != null) {
            dependedOnBy.remove(toDoItem);
        }
    }

    public void addContext(String context) {
        if (contexts == null) {
            contexts = new ArrayList<>();
        }
        if (!contexts.contains(context)) {
            contexts.add(context);
        }
    }

    public void removeContext(String context) {
        if (contexts != null) {
            contexts.remove(context);
        }
    }

    public boolean isDoable() {
        if (!dependsOn.isEmpty()) {
            return false;
        }
        return start == null || !start.isAfter(LocalDate.now());
    }

    // Getters and Setters
    //////////////////////

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

    @Override
    public String toString() {
        return "ToDoItem{" +
                "title=" + title +
                ", description='" + description + '\'' +
                ", deadline=" + deadline +
                ", start=" + start +
                ", dependsOn=" + dependsOn +
                ", dependedOnBy=" + dependedOnBy +
                ", contexts=" + contexts +
                ", isRecurrent=" + isRecurrent +
                ", recurringPattern=" + recurringPattern +
                '}';
    }
}
