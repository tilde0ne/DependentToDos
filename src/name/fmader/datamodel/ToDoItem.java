package name.fmader.datamodel;

import javafx.beans.property.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ToDoItem implements Serializable {

    private static final long serialVersionUID = -5027435631969990301L;

    protected LocalDate created;
    protected transient StringProperty title;
    protected String description;

    protected transient ObjectProperty<LocalDate> deadline = new SimpleObjectProperty<>();
    protected LocalDate originalDeadline;
    protected transient ObjectProperty<LocalDate> start = new SimpleObjectProperty<>();

    protected transient BooleanProperty doable = new SimpleBooleanProperty();

    protected List<ToDoItem> children;
    protected List<ToDoItem> parents;
    protected List<String> contexts;

    protected boolean isRecurrent;
    protected RecurringPattern recurringPattern;
    protected boolean hasFollowUp;

    public ToDoItem(String title) {
        this.created = LocalDate.now();
        this.title = new SimpleStringProperty(title);
        this.children = new ArrayList<>();
        this.parents = new ArrayList<>();
        this.contexts = new ArrayList<>();
        this.doable.set(true);
    }

    public void addChild(ToDoItem toDoItem) {
        if (!children.contains(toDoItem)) {
            children.add(toDoItem);
            toDoItem.addParent(this);
            toDoItem.recalculateDeadline();
        }
        doable.set(false);
    }

    public void removeChild(ToDoItem toDoItem) {
        children.remove(toDoItem);
        toDoItem.removeParent(this);
        toDoItem.recalculateDeadline();
        doable.set((start.get() == null || !start.get().isAfter(LocalDate.now())) && children.isEmpty());
    }

    public void addParent(ToDoItem toDoItem) {
        if (!parents.contains(toDoItem)) {
            parents.add(toDoItem);
        }
    }

    public void removeParent(ToDoItem toDoItem) {
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

    public void recalculateDeadline() {
        setDeadline(originalDeadline);
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

    protected LocalDate checkAgainstParentDeadlines(LocalDate deadline) {
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
        doable = new SimpleBooleanProperty((start.get() == null || !start.get().isAfter(LocalDate.now())) && children.isEmpty());
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

    public LocalDate getOriginalDeadline() {
        return originalDeadline;
    }

    public void setOriginalDeadline(LocalDate originalDeadline) {
        this.originalDeadline = originalDeadline;
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

    public boolean getDoable() {
        return doable.get();
    }

    public BooleanProperty doableProperty() {
        return doable;
    }

    public void setDoable(boolean doable) {
        this.doable.set(doable);
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
                "\n, doable=" + doable.get() +
                "\n, isRecurrent=" + isRecurrent +
                "\n, recurringPattern=" + recurringPattern +
                "\n, hasFollowUp=" + hasFollowUp +
                "\n}";
    }
}