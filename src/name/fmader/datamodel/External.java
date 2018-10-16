package name.fmader.datamodel;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;

public class External extends ToDoItem {

    private static final long serialVersionUID = -3022816578558344618L;

    public External(String title) {
        super(title);
    }

    @Override
    public boolean isInherited() {
        if (getInheritedDeadline() == null) {
            return false;
        }
        if (getOriginalDeadline() == null) {
            return true;
        }
        return !getInheritedDeadline().equals(getOriginalDeadline());
    }

    @Override
    public void setDeadline(LocalDate deadline) {
        setOriginalDeadline(deadline);
        deadlineProperty().set(deadline);

        deadline = checkAgainstParentDeadlines(deadline);
        setInheritedDeadline(deadline);
        inheritedProperty().set(isInherited());
        if (!isInherited()) {
            setInheritedDeadline(null);
        }

        if (!getChildren().isEmpty()) {
            for (ToDoItem toDoItem : getChildren()) {
                toDoItem.recalculateDeadline();
            }
        }
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeUTF(titleProperty().get());
        out.writeObject(deadlineProperty().get());
        out.writeObject(startProperty().get());
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        initProperties();
        titleProperty().set(in.readUTF());
        deadlineProperty().set((LocalDate) in.readObject());
        startProperty().set((LocalDate) in.readObject());
        dependentProperty().set(!getChildren().isEmpty());
        inheritedProperty().set(isInherited());
    }

    @Override
    public String toString() {
        return "External{" +
                "\ntitle=" + getTitle() +
                "\n, description='" + getDescription() + '\'' +
                "\n, deadline=" + getDeadline() +
                "\n, originalDeadline=" + getOriginalDeadline() +
                "\n, inheritedDeadline=" + getInheritedDeadline() +
                "\n, start=" + getStart() +
                "\n, children=" + getChildren().size() +
                "\n, parents=" + getParents().size() +
                "\n, contexts=" + getContexts() +
                "\n, doable=" + getDependent() +
                "\n, isRecurrent=" + isRecurrent() +
                "\n, recurringPattern=" + getRecurringPattern() +
                "\n, hasFollowUp=" + hasFollowUp() +
                "\n}";
    }
}
