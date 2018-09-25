package name.fmader.datamodel;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Appointment extends ToDoItem {

    private static final long serialVersionUID = -226146364586406102L;

    public Appointment(String title, LocalDate date, LocalTime time) {
        super(title);
        setDeadline(date);
        setOriginalDeadline(date);
        setDateTime(date.atTime(time));
    }

    @Override
    public boolean isInherited() {
        if (getInheritedDeadline() == null) {
            return false;
        }
        if (getOriginalDeadline() == null) {
            return true;
        }
        return !getInheritedDeadline().equals(getOriginalDeadline());
    }

    @Override
    public void setDeadline(LocalDate deadline) {
        setOriginalDeadline(deadline);
        deadlineProperty().set(deadline);

        deadline = checkAgainstParentDeadlines(deadline);
        setInheritedDeadline(deadline);
        if (!isInherited()) {
            setInheritedDeadline(null);
        }

        if (!getChildren().isEmpty()) {
            for (ToDoItem toDoItem : getChildren()) {
                toDoItem.recalculateDeadline();
            }
        }
    }

    @Override
    public void setStart(LocalDate start) {
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeUTF(titleProperty().get());
        out.writeObject(deadlineProperty().get());
        out.writeObject(dateTimeProperty().get());
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        initProperties();
        titleProperty().set(in.readUTF());
        deadlineProperty().set((LocalDate) in.readObject());
        dateTimeProperty().set((LocalDateTime) in.readObject());
        dependentProperty().set(!getChildren().isEmpty());
    }

    @Override
    public String toString() {
        return "Appointment{" +
                "\ntitle=" + getTitle() +
                "\n, dateTime=" + getDateTime() +
                "\n, description='" + getDescription() + '\'' +
                "\n, deadline=" + getDeadline() +
                "\n, originalDeadline=" + getOriginalDeadline() +
                "\n, inheritedDeadline=" + getInheritedDeadline() +
                "\n, start=" + getStart() +
                "\n, children=" + getChildren().size() +
                "\n, parents=" + getParents().size() +
                "\n, contexts=" + getContexts() +
                "\n, doable=" + getDependent() +
                "\n, isRecurrent=" + isRecurrent() +
                "\n, recurringPattern=" + getRecurringPattern() +
                "\n, hasFollowUp=" + hasFollowUp() +
                "\n}";
    }
}
