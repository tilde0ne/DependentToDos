package name.fmader.datamodel;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Appointment extends ToDoItem {

    private static final long serialVersionUID = -5854590359242697046L;

    private transient SimpleObjectProperty<LocalDateTime> dateTime;

    public Appointment(String title, LocalDate date, LocalTime time) {
        super(title);
        this.deadline = new SimpleObjectProperty<>(date);
        this.dateTime = new SimpleObjectProperty<>(date.atTime(time));
    }

    @Override
    public boolean isDoable() {
        return dependsOn.isEmpty();
    }

    @Override
    public LocalDate getStart() {
        return null;
    }

    @Override
    public void setStart(LocalDate start) {
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
    }

    public LocalDateTime getDateTime() {
        return dateTime.get();
    }

    public SimpleObjectProperty<LocalDateTime> dateTimeProperty() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime.set(dateTime);
    }

    @Override
    public String toString() {
        return "Appointment{" +
                "title=" + title.get() +
                ", dateTime=" + dateTime.get() +
                ", description='" + description + '\'' +
                ", deadline=" + deadline.get() +
                ", start=" + start +
                ", dependsOn=" + dependsOn.size() +
                ", dependedOnBy=" + dependedOnBy.size() +
                ", contexts=" + contexts +
                ", isRecurrent=" + isRecurrent +
                ", recurringPattern=" + recurringPattern +
                '}';
    }
}
