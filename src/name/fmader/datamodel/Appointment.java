package name.fmader.datamodel;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Appointment extends ToDoItem {

    private static final long serialVersionUID = -226146364586406102L;

    private transient ObjectProperty<LocalDateTime> dateTime;
    private LocalDate inheritedDeadline;

    public Appointment(String title, LocalDate date, LocalTime time) {
        super(title);
        this.deadline = new SimpleObjectProperty<>(date);
        this.originalDeadline = date;
        this.dateTime = new SimpleObjectProperty<>(date.atTime(time));
    }

    @Override
    public boolean isDoable() {
        return !isDependent.get();
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

        LocalTime temp = dateTime.get().toLocalTime();
        dateTime.set(this.deadline.get().atTime(temp));

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

    @Override
    public void setStart(LocalDate start) {
    }

    public LocalDate getInheritedDeadline() {
        return inheritedDeadline;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeUTF(title.get());
        out.writeObject(deadline.get());
        out.writeObject(dateTime.get());
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        title = new SimpleStringProperty(in.readUTF());
        deadline = new SimpleObjectProperty<>((LocalDate) in.readObject());
        dateTime = new SimpleObjectProperty<>((LocalDateTime) in.readObject());
        start = new SimpleObjectProperty<>();
        isDependent = new SimpleBooleanProperty(!children.isEmpty());
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
                "\ntitle=" + title.get() +
                "\n, dateTime=" + dateTime.get() +
                "\n, description='" + description + '\'' +
                "\n, deadline=" + deadline.get() +
                "\n, originalDeadline=" + originalDeadline +
                "\n, inheritedDeadline=" + inheritedDeadline +
                "\n, start=" + start.get() +
                "\n, children=" + children.size() +
                "\n, parents=" + parents.size() +
                "\n, contexts=" + contexts +
                "\n, isDependent=" + isDependent.get() +
                "\n, isRecurrent=" + isRecurrent +
                "\n, recurringPattern=" + recurringPattern +
                "\n, hasFollowUp=" + hasFollowUp +
                "\n}";
    }
}
