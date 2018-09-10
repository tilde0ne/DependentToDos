package name.fmader.datamodel;

import java.time.LocalDate;

public class Appointment extends ToDoItem {

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

    @Override
    public String toString() {
        return "Appointment{" +
                "title=" + title +
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
