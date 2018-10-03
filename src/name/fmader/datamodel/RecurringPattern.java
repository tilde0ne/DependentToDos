package name.fmader.datamodel;

import java.io.Serializable;

public class RecurringPattern implements Serializable {

    private static final long serialVersionUID = 2141473658200795374L;

    /*fix:

    removed the idea of fix for now.

    a new item is created on done().

    if deadline(original) is set, counting starts from deadline, start is set if in future.
    else start will be set, counting from now().*/

    private int everyN;
    private RecurringBase recurringBase;

    public RecurringPattern(RecurringBase base, int everyN) {
        this.recurringBase = base;
        this.everyN = everyN;
    }

    public int getEveryN() {
        return everyN;
    }

    public RecurringBase getRecurringBase() {
        return recurringBase;
    }

    @Override
    public String toString() {
        return "RecurringPattern{" +
                "\n, everyN=" + everyN +
                "\n, recurringBase=" + recurringBase +
                "\n}";
    }
}
