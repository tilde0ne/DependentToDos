package name.fmader.datamodel;

import java.io.Serializable;
import java.time.LocalDate;

public class RecurringPattern implements Serializable {

    private static final long serialVersionUID = -7392251297239683590L;

    /*fix:

    if false (only valid if NO deadline is specified), a new item will be created when done() is called,
    start will be as values dictate;

    if true AND NO deadline is specified, a new item will be created with the specified offset to when
    current item is created (or if specified reaches start date);

    if true AND a deadline is specified, a new item will be created when current item is due,
    deadline will be as values dictate, start will be added according to the current start-due range
    if it lies in the future at creation date;*/
    private boolean fix;

    private int everyNDays;
    private int everyNMonths;
    private int everyNYears;

    private LocalDate recurrencyEnds;

    public RecurringPattern(boolean fix, RecurringBase base, int baseUnits) {
        this.fix = fix;
        switch (base) {
            case EVERYNDAYS:
                this.everyNDays = baseUnits;
                break;
            case EVERYNWEEKS:
                this.everyNDays = 7 * baseUnits;
                break;
            case EVERYNMONTHS:
                this.everyNMonths = baseUnits;
                break;
            case EVERYNYEARS:
                this.everyNYears = baseUnits;
                break;
            default:
                this.recurrencyEnds = LocalDate.now();
                break;
        }
    }

    @Override
    public String toString() {
        return "RecurringPattern{" +
                "\nfix=" + fix +
                "\n, everyNDays=" + everyNDays +
                "\n, everyNMonths=" + everyNMonths +
                "\n, everyNYears=" + everyNYears +
                "\n, recurrencyEnds=" + recurrencyEnds +
                "\n}";
    }
}
