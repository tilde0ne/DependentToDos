package name.fmader.datamodel;

import java.io.Serializable;

public class RecurringPattern implements Serializable {

    /*
    fix: if false, counting starts when item's done() is called;
    if true, counting starts when current item is created
    */
    private boolean fix;
    private boolean dateDependand;
}
