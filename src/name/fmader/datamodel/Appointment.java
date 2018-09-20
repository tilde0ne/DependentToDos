package name.fmader.datamodel;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Appointment extends ToDoItem {

    private static final long serialVersionUID = -226146364586406102L;

    private transient ObjectProperty<LocalDateTime> dateTime = new SimpleObjectProperty<>();
    private LocalDate inheritedDeadline;

    public Appointment(String title, LocalDate date, LocalTime time) {
        super(title);
        setDeadline(date);
        setOriginalDeadline(date);
        dateTime.set(date.atTime(time));
    }

    @Override
    public boolean isInherited() {
        if (inheritedDeadline == null) {
            return false;
        }
        if (getOriginalDeadline() == null) {
            return true;
        }
        return !inheritedDeadline.equals(getOriginalDeadline());
    }

    @Override
    public void setDeadline(LocalDate deadline) {
        setOriginalDeadline(deadline);
        deadlineProperty().set(deadline);

        LocalTime temp = dateTime.get().toLocalTime();
        dateTime.set(deadlineProperty().get().atTime(temp));

        deadline = checkAgainstParentDeadlines(deadline);
        inheritedDeadline = deadline;
        if (!isInherited()) {
            inheritedDeadline = null;
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

    public LocalDate getInheritedDeadline() {
        return inheritedDeadline;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeUTF(getTitle());
        out.writeObject(getDeadline());
        out.writeObject(getDateTime());
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        setTitle(in.readUTF());
        setDeadline((LocalDate) in.readObject());
        setDateTime((LocalDateTime) in.readObject());
        setStart(null);
        setDoable(getChildren().isEmpty());
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

    @Override
    public String toString() {
        return "Appointment{" +
                "\ntitle=" + getTitle() +
                "\n, dateTime=" + dateTime.get() +
                "\n, description='" + getDescription() + '\'' +
                "\n, deadline=" + getDeadline() +
                "\n, originalDeadline=" + getOriginalDeadline() +
                "\n, inheritedDeadline=" + inheritedDeadline +
                "\n, start=" + getStart() +
                "\n, children=" + getChildren().size() +
                "\n, parents=" + getParents().size() +
                "\n, contexts=" + getContexts() +
                "\n, doable=" + isDoable() +
                "\n, isRecurrent=" + isRecurrent() +
                "\n, recurringPattern=" + getRecurringPattern() +
                "\n, hasFollowUp=" + hasFollowUp() +
                "\n}";
    }
}
