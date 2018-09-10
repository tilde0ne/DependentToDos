package name.fmader.datamodel;

import java.time.LocalDate;

public class Project extends ToDoItem{

    private static final long serialVersionUID = 3665790250284612759L;

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
                "title=" + title.get() +
                ", description='" + description + '\'' +
                ", deadline=" + (deadline == null ? null : deadline.get()) +
                ", start=" + start +
                ", dependsOn=" + (dependsOn == null ? null : dependsOn.size()) +
                ", dependedOnBy=" + (dependedOnBy == null ? null : dependedOnBy.size()) +
                ", contexts=" + contexts +
                ", isRecurrent=" + isRecurrent +
                ", recurringPattern=" + recurringPattern +
                '}';
    }
}
