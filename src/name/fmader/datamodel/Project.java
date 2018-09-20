package name.fmader.datamodel;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;

public class Project extends ToDoItem{

    private static final long serialVersionUID = 3665790250284612759L;

    public Project(String title) {
        super("[P] " + title);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeUTF(getTitle());
        out.writeObject(getDeadline() == null ? LocalDate.of(1, 1, 1) : getDeadline());
        out.writeObject(getStart() == null ? LocalDate.of(1, 1, 1) : getStart());
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        setTitle(in.readUTF());
        LocalDate date = (LocalDate) in.readObject();
        if (!date.equals(LocalDate.of(1, 1, 1))) {
            setDeadline(date);
        }
        date = (LocalDate) in.readObject();
        if (!date.equals(LocalDate.of(1, 1, 1))) {
            setStart(date);
        }
        setDoable((getStart() == null || !getStart().isAfter(LocalDate.now())) && getChildren().isEmpty());
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
                "\ntitle=" + getTitle() +
                "\n, description='" + getDescription() + '\'' +
                "\n, deadline=" + getDeadline() +
                "\n, originalDeadline=" + getOriginalDeadline() +
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
