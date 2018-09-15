package name.fmader.datamodel;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;

public class Project extends ToDoItem{

    private static final long serialVersionUID = 3665790250284612759L;

    public Project(String title) {
        super("[P] " + title);
    }

    @Override
    public boolean isDoable() {
        if (start != null && start.isAfter(LocalDate.now())) {
            return false;
        }
        if (children == null || children.isEmpty()) {
            return true;
        }
        for (ToDoItem toDoItem : children) {
            if (toDoItem.isDoable()) {
                return true;
            }
        }
        return false;
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

    @Override
    public boolean isRecurrent() {
        return false;
    }

    @Override
    public void setRecurrent(boolean recurrent) {
    }

    @Override
    public RecurringPattern getRecurringPattern() {
        return null;
    }

    @Override
    public void setRecurringPattern(RecurringPattern recurringPattern) {
    }

    @Override
    public String toString() {
        return "Project{" +
                "\ntitle=" + title.get() +
                "\n, description='" + description + '\'' +
                "\n, deadline=" + (deadline == null ? null : deadline.get()) +
                "\n, originalDeadline=" + originalDeadline +
                "\n, start=" + start +
                "\n, children=" + children.size() +
                "\n, parents=" + parents.size() +
                "\n, contexts=" + contexts +
                "\n, isRecurrent=" + isRecurrent +
                "\n, recurringPattern=" + recurringPattern +
                "\n, hasFollowUp=" + hasFollowUp +
                "\n}";
    }
}
