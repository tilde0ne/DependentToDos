package name.fmader.datamodel;

import java.io.Serializable;
import java.time.LocalDate;

public class RecurringPattern implements Serializable {

    private static final long serialVersionUID = 2141473658200795374L;

    /*fix:

    if false (only valid if NO deadline is specified), a new item will be created when done() is called,
    start will be as values dictate;

    if true AND NO deadline is specified, a new item will be created with the specified offset to when
    current item is created (or if specified reaches start date);

    if true AND a deadline is specified, a new item will be created when current item is due,
    deadline will be as values dictate, start will be added according to the current start-due range
    if it lies in the future at creation date;*/
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
