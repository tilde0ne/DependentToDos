package name.fmader.datamodel;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;

public class External extends ToDoItem {

    private static final long serialVersionUID = -4447036618223208643L;

    private LocalDate inheritedDeadline;

    public External(String title) {
        super(title);
    }

    @Override
    public boolean isInherited() {
        if (inheritedDeadline == null) {
            return false;
        }
        if (originalDeadline == null) {
            return true;
        }
        return !inheritedDeadline.equals(originalDeadline);
    }

    @Override
    public void setDeadline(LocalDate deadline) {
        originalDeadline = deadline;

        if (deadline == null) {
            this.deadline = null;
        } else if (this.deadline == null) {
            this.deadline = new SimpleObjectProperty<>(deadline);
        } else {
            this.deadline.set(deadline);
        }

        deadline = checkAgainstParentDeadlines(deadline);
        inheritedDeadline = deadline;

        if (dependsOn != null && !dependsOn.isEmpty()) {
            for (ToDoItem toDoItem : dependsOn) {
                toDoItem.recalculateDeadline();
            }
        }
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
    public String toString() {
        return "External{" +
                "title=" + title.get() +
                ", description='" + description + '\'' +
                ", deadline=" + (deadline == null ? null : deadline.get()) +
                ", originalDeadline=" + originalDeadline +
                ", inheritedDeadline=" + inheritedDeadline +
                ", start=" + start +
                ", dependsOn=" + dependsOn.size() +
                ", dependedOnBy=" + dependedOnBy.size() +
                ", contexts=" + contexts +
                ", isRecurrent=" + isRecurrent +
                ", recurringPattern=" + recurringPattern +
                '}';
    }
}
