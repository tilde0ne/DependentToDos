package name.fmader.datamodel;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;

public class External extends ToDoItem {

    private static final long serialVersionUID = -3022816578558344618L;

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
        this.deadline.set(deadline);

        deadline = checkAgainstParentDeadlines(deadline);
        inheritedDeadline = deadline;
        if (!isInherited()) {
            inheritedDeadline = null;
        }

        if (!children.isEmpty()) {
            for (ToDoItem toDoItem : children) {
                toDoItem.recalculateDeadline();
            }
        }
    }

    public LocalDate getInheritedDeadline() {
        return inheritedDeadline;
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

    @Override
    public String toString() {
        return "External{" +
                "\ntitle=" + title.get() +
                "\n, description='" + description + '\'' +
                "\n, deadline=" + (deadline.get()) +
                "\n, originalDeadline=" + originalDeadline +
                "\n, inheritedDeadline=" + inheritedDeadline +
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
