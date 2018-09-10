package name.fmader.datamodel;

import javafx.beans.property.SimpleObjectProperty;

import java.time.LocalDate;
import java.time.LocalTime;

public class Appointment extends ToDoItem {

    private static final long serialVersionUID = 4313963647120775218L;

    private SimpleObjectProperty<LocalTime> time;

    public Appointment(String title, LocalDate date, LocalTime time) {
        super(title);
        this.deadline = new SimpleObjectProperty<>(date);
        this.time = new SimpleObjectProperty<>(time);
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

    public LocalTime getTime() {
        return time.get();
    }

    public SimpleObjectProperty<LocalTime> timeProperty() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time.set(time);
    }

    @Override
    public String toString() {
        return "Appointment{" +
                "title=" + title.get() +
                ", time=" + time.get() +
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
