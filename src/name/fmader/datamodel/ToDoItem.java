package name.fmader.datamodel;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ToDoItem implements Serializable {

    private static final long serialVersionUID = -6394894748043619311L;

    protected LocalDate created;
    protected transient SimpleStringProperty title;
    protected String description;

    protected transient SimpleObjectProperty<LocalDate> deadline;
    protected LocalDate originalDeadline;
    protected LocalDate start;

    protected List<ToDoItem> dependsOn;
    protected List<ToDoItem> dependedOnBy;
    protected List<String> contexts;

    protected boolean isRecurrent;
    protected RecurringPattern recurringPattern;
    protected boolean hasFollowUp;

    public ToDoItem(String title) {
        this.created = LocalDate.now();
        this.title = new SimpleStringProperty(title);
        this.dependsOn = new ArrayList<>();
        this.dependedOnBy = new ArrayList<>();
        this.contexts = new ArrayList<>();
    }

    public void addDependsOn(ToDoItem toDoItem) {
        if (!dependsOn.contains(toDoItem)) {
            dependsOn.add(toDoItem);
            toDoItem.addDependedOnBy(this);
            toDoItem.recalculateDeadline();
        }
    }

    public void removeDependsOn(ToDoItem toDoItem) {
        dependsOn.remove(toDoItem);
        toDoItem.removeDependedOnBy(this);
        toDoItem.recalculateDeadline();
    }

    public void addDependedOnBy(ToDoItem toDoItem) {
        if (!dependedOnBy.contains(toDoItem)) {
            dependedOnBy.add(toDoItem);
        }
    }

    public void removeDependedOnBy(ToDoItem toDoItem) {
        dependedOnBy.remove(toDoItem);
    }

    public void addContext(String context) {
        if (!contexts.contains(context)) {
            contexts.add(context);
        }
    }

    public void removeContext(String context) {
        contexts.remove(context);
    }

    public boolean isDoable() {
        if (start == null || !start.isAfter(LocalDate.now())) {
            return dependsOn.isEmpty();
        }
        return false;
    }

    public void recalculateDeadline() {
        setDeadline(originalDeadline);
    }

    public boolean isInherited() {
        if (deadline == null) {
            return false;
        }
        if (originalDeadline == null) {
            return true;
        }
        return !deadline.get().equals(originalDeadline);
    }

    protected LocalDate checkAgainstParentDeadlines(LocalDate deadline) {
        if (dependedOnBy != null && !dependedOnBy.isEmpty()) {
            for (ToDoItem toDoItem : dependedOnBy) {
                if (toDoItem.deadline == null) {
                    continue;
                }
                LocalDate parentDeadline = toDoItem.deadline.get();
                if (deadline == null || deadline.isAfter(parentDeadline)) {
                    deadline = parentDeadline;
                }
            }
        }
        return deadline;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeUTF(title.get());
        out.writeObject(deadline == null ? LocalDate.of(1, 1, 1) : deadline.get());
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        title = new SimpleStringProperty(in.readUTF());
        LocalDate date = (LocalDate) in.readObject();
        if (!date.equals(LocalDate.of(1, 1, 1))) {
            deadline = new SimpleObjectProperty<>(date);
        }
    }

    // Getters and Setters
    //////////////////////

    public LocalDate getCreated() {
        return created;
    }

    public void setCreated(LocalDate created) {
        this.created = created;
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
        originalDeadline = deadline;

        deadline = checkAgainstParentDeadlines(deadline);
        if (deadline == null) {
            this.deadline = null;
        } else if (this.deadline == null) {
            this.deadline = new SimpleObjectProperty<>(deadline);
        } else {
            this.deadline.set(deadline);
        }

        if (dependsOn != null && !dependsOn.isEmpty()) {
            for (ToDoItem toDoItem : dependsOn) {
                toDoItem.recalculateDeadline();
            }
        }
    }

    public LocalDate getOriginalDeadline() {
        return originalDeadline;
    }

    public void setOriginalDeadline(LocalDate originalDeadline) {
        this.originalDeadline = originalDeadline;
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

    public boolean hasFollowUp() {
        return hasFollowUp;
    }

    public void setHasFollowUp(boolean hasFollowUp) {
        this.hasFollowUp = hasFollowUp;
    }

    @Override
    public String toString() {
        return "ToDoItem{" +
                "title=" + title.get() +
                ", description='" + description + '\'' +
                ", deadline=" + (deadline == null ? null : deadline.get()) +
                ", originalDeadline=" + originalDeadline +
                ", start=" + start +
                ", dependsOn=" + dependsOn.size() +
                ", dependedOnBy=" + dependedOnBy.size() +
                ", contexts=" + contexts +
                ", isRecurrent=" + isRecurrent +
                ", recurringPattern=" + recurringPattern +
                ", hasFollowUp=" + hasFollowUp +
                '}';
    }
}