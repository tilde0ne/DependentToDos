package name.fmader.datamodel;

public class External extends ToDoItem {

    private static final long serialVersionUID = -4447036618223208643L;

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
