package name.fmader.datamodel;

import java.io.Serializable;
import java.time.LocalDate;

public class RecurringPattern implements Serializable {

    private static final long serialVersionUID = 2141473658200795374L;

    /*fix:

    removed the idea of fix for now.

    a new item is created on done().

    if deadline(original) is set, counting starts from deadline, start is set if in future.
    else start will be set, counting from now().*/

    private boolean fix;
    private int everyN;
    private RecurringBase recurringBase;
    private LocalDate recurringEnds;

    public RecurringPattern(boolean fix, RecurringBase base, int everyN) {
        this.fix = fix;
        this.recurringBase = base;
        this.everyN = everyN;
    }

    public boolean isFix() {
        return fix;
    }

    public int getEveryN() {
        return everyN;
    }

    public RecurringBase getRecurringBase() {
        return recurringBase;
    }

    public LocalDate getRecurringEnds() {
        return recurringEnds;
    }

    @Override
    public String toString() {
        return "RecurringPattern{" +
                "\nfix=" + fix +
                "\n, everyN=" + everyN +
                "\n, recurringBase=" + recurringBase +
                "\n, recurringEnds=" + recurringEnds +
                "\n}";
    }
}
