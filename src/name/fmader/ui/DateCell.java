package name.fmader.ui;

import javafx.scene.control.TableCell;
import name.fmader.datamodel.ToDoItem;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateCell extends TableCell<ToDoItem, LocalDate> {

    @Override
    protected void updateItem(LocalDate item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
            setText(null);
        } else {
            setText(item.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        }
    }
}
