package name.fmader.datamodel;

import javafx.beans.property.SimpleObjectProperty;

import java.time.LocalDate;
import java.time.LocalTime;

public class Appointment extends ToDoItem {

    private static final long serialVersionUID = -5873691858014094083L;

    private SimpleObjectProperty<LocalTime> time;

    public Appointment(String title) {
        super(title);
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
                "time=" + time +
                ", title=" + title +
                ", description='" + description + '\'' +
                ", deadline=" + deadline +
                ", start=" + start +
                ", dependsOn=" + dependsOn +
                ", dependedOnBy=" + dependedOnBy +
                ", contexts=" + contexts +
                ", isRecurrent=" + isRecurrent +
                ", recurringPattern=" + recurringPattern +
                '}';
    }
}
