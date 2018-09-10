package name.fmader.datamodel;

import java.time.LocalDate;

public class Project extends ToDoItem{

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
