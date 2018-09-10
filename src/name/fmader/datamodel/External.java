package name.fmader.datamodel;

public class External extends ToDoItem {

    public External(String title) {
        super(title);
    }

    @Override
    public String toString() {
        return "External{" +
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
