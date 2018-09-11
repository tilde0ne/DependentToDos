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
        super(title);
    }

    @Override
    public boolean isDoable() {
        if (start != null && start.isAfter(LocalDate.now())) {
            return false;
        }
        if (dependsOn == null || dependsOn.isEmpty()) {
            return true;
        }
        for (ToDoItem toDoItem : dependsOn) {
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
                "title=" + title.get() +
                ", description='" + description + '\'' +
                ", deadline=" + (deadline == null ? null : deadline.get()) +
                ", start=" + start +
                ", dependsOn=" + dependsOn.size() +
                ", dependedOnBy=" + dependedOnBy.size() +
                ", contexts=" + contexts +
                ", isRecurrent=" + isRecurrent +
                ", recurringPattern=" + recurringPattern +
                '}';
    }
}
