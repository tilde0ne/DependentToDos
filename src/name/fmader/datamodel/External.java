package name.fmader.datamodel;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;

public class External extends ToDoItem {

    private static final long serialVersionUID = -3022816578558344618L;

    private LocalDate inheritedDeadline;

    public External(String title) {
        super(title);
    }

    @Override
    public boolean isInherited() {
        if (inheritedDeadline == null) {
            return false;
        }
        if (getOriginalDeadline() == null) {
            return true;
        }
        return !inheritedDeadline.equals(getOriginalDeadline());
    }

    @Override
    public void setDeadline(LocalDate deadline) {
        setOriginalDeadline(deadline);
        deadlineProperty().set(deadline);

        deadline = checkAgainstParentDeadlines(deadline);
        inheritedDeadline = deadline;
        if (!isInherited()) {
            inheritedDeadline = null;
        }

        if (!getChildren().isEmpty()) {
            for (ToDoItem toDoItem : getChildren()) {
                toDoItem.recalculateDeadline();
            }
        }
    }

    public LocalDate getInheritedDeadline() {
        return inheritedDeadline;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeUTF(getTitle());
        out.writeObject(getDeadline() == null ? LocalDate.of(1, 1, 1) : getDeadline());
        out.writeObject(getStart() == null ? LocalDate.of(1, 1, 1) : getStart());
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        titleProperty().set(in.readUTF());
        LocalDate date = (LocalDate) in.readObject();
        if (!date.equals(LocalDate.of(1, 1, 1))) {
            deadlineProperty().set(date);
        }
        date = (LocalDate) in.readObject();
        if (!date.equals(LocalDate.of(1, 1, 1))) {
            startProperty().set(date);
        }
        doableProperty().set((getStart() == null || !getStart().isAfter(LocalDate.now())) && getChildren().isEmpty());
    }

    @Override
    public String toString() {
        return "External{" +
                "\ntitle=" + getTitle() +
                "\n, description='" + getDescription() + '\'' +
                "\n, deadline=" + getDeadline() +
                "\n, originalDeadline=" + getOriginalDeadline() +
                "\n, inheritedDeadline=" + inheritedDeadline +
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
