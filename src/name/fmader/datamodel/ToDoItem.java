package name.fmader.datamodel;

import javafx.beans.property.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ToDoItem implements Serializable {

    private static final long serialVersionUID = -5027435631969990301L;

    private LocalDate created;
    private transient StringProperty title = new SimpleStringProperty();
    private String description;

    private transient ObjectProperty<LocalDate> deadline = new SimpleObjectProperty<>();
    private transient ObjectProperty<LocalDateTime> dateTime = new SimpleObjectProperty<>();
    private LocalDate originalDeadline;
    private LocalDate inheritedDeadline;
    private transient ObjectProperty<LocalDate> start = new SimpleObjectProperty<>();

    private transient BooleanProperty dependent = new SimpleBooleanProperty();

    private List<ToDoItem> children = new ArrayList<>();
    private List<ToDoItem> parents = new ArrayList<>();
    private List<String> contexts = new ArrayList<>();

    private boolean isRecurrent;
    private RecurringPattern recurringPattern;
    private boolean hasFollowUp;

    public ToDoItem(String title) {
        this.created = LocalDate.now();
        this.title.set(title);
        this.dependent.set(false);
    }

    public void addChild(ToDoItem toDoItem) {
        if (!children.contains(toDoItem)) {
            children.add(toDoItem);
            toDoItem.addParent(this);
            toDoItem.recalculateDeadline();
        }
        dependent.set(true);
    }

    public void removeChild(ToDoItem toDoItem) {
        children.remove(toDoItem);
        toDoItem.removeParent(this);
        toDoItem.recalculateDeadline();
        dependent.set(!children.isEmpty());
    }

    private void addParent(ToDoItem toDoItem) {
        if (!parents.contains(toDoItem)) {
            parents.add(toDoItem);
        }
    }

    private void removeParent(ToDoItem toDoItem) {
        parents.remove(toDoItem);
    }

    public void addContext(String context) {
        if (!contexts.contains(context)) {
            contexts.add(context);
        }
    }

    public void removeContext(String context) {
        contexts.remove(context);
    }

    public boolean isInherited() {
        if (deadline.get() == null) {
            return false;
        }
        if (originalDeadline == null) {
            return true;
        }
        return !deadline.get().equals(originalDeadline);
    }

    void recalculateDeadline() {
        setDeadline(originalDeadline);
    }

    LocalDate checkAgainstParentDeadlines(LocalDate deadline) {
        for (ToDoItem toDoItem : parents) {
            if (toDoItem.deadline.get() == null) {
                continue;
            }
            LocalDate parentDeadline = toDoItem.deadline.get();
            if (deadline == null || deadline.isAfter(parentDeadline)) {
                deadline = parentDeadline;
            }
        }
        return deadline;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeUTF(title.get());
        out.writeObject(deadline.get() == null ? LocalDate.of(1, 1, 1) : deadline.get());
        out.writeObject(start.get() == null ? LocalDate.of(1, 1, 1) : start.get());
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        initProperties();
        title = new SimpleStringProperty(in.readUTF());
        LocalDate date = (LocalDate) in.readObject();
        if (!date.equals(LocalDate.of(1, 1, 1))) {
            deadline = new SimpleObjectProperty<>(date);
        } else {
            deadline = new SimpleObjectProperty<>();
        }
        date = (LocalDate) in.readObject();
        if (!date.equals(LocalDate.of(1, 1, 1))) {
            start = new SimpleObjectProperty<>(date);
        } else {
            start = new SimpleObjectProperty<>();
        }
        dependent = new SimpleBooleanProperty(!children.isEmpty());
    }

    void initProperties() {
        title = new SimpleStringProperty();
        deadline = new SimpleObjectProperty<>();
        dateTime = new SimpleObjectProperty<>();
        start = new SimpleObjectProperty<>();
        dependent = new SimpleBooleanProperty();
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

    public StringProperty titleProperty() {
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
        return deadline.get();
    }

    public ObjectProperty<LocalDate> deadlineProperty() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        originalDeadline = deadline;

        deadline = checkAgainstParentDeadlines(deadline);
        this.deadline.set(deadline);

        if (!children.isEmpty()) {
            for (ToDoItem toDoItem : children) {
                toDoItem.recalculateDeadline();
            }
        }
    }

    public LocalDateTime getDateTime() {
        return dateTime.get();
    }

    public ObjectProperty<LocalDateTime> dateTimeProperty() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime.set(dateTime);
    }

    public LocalDate getOriginalDeadline() {
        return originalDeadline;
    }

    public void setOriginalDeadline(LocalDate originalDeadline) {
        this.originalDeadline = originalDeadline;
    }

    public LocalDate getInheritedDeadline() {
        return inheritedDeadline;
    }

    public void setInheritedDeadline(LocalDate inheritedDeadline) {
        this.inheritedDeadline = inheritedDeadline;
    }

    public LocalDate getStart() {
        return start.get();
    }

    public ObjectProperty<LocalDate> startProperty() {
        return start;
    }

    public void setStart(LocalDate start) {
        this.start.set(start);
    }

    public List<ToDoItem> getChildren() {
        return children;
    }

    public List<ToDoItem> getParents() {
        return parents;
    }

    public List<String> getContexts() {
        return contexts;
    }

    public void setContexts(List<String> contexts) {
        this.contexts = contexts;
    }

    public boolean getDependent() {
        return dependent.get();
    }

    public BooleanProperty dependentProperty() {
        return dependent;
    }

    public void setDependent(boolean dependent) {
        this.dependent.set(dependent);
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
                "\ntitle=" + title.get() +
                "\n, description='" + description + '\'' +
                "\n, deadline=" + deadline.get() +
                "\n, originalDeadline=" + originalDeadline +
                "\n, start=" + start.get() +
                "\n, children=" + children.size() +
                "\n, parents=" + parents.size() +
                "\n, contexts=" + contexts +
                "\n, dependent=" + dependent.get() +
                "\n, isRecurrent=" + isRecurrent +
                "\n, recurringPattern=" + recurringPattern +
                "\n, hasFollowUp=" + hasFollowUp +
                "\n}";
    }
}