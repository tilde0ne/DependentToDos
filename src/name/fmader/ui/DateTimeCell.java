package name.fmader.ui;

import javafx.scene.control.TableCell;
import name.fmader.datamodel.ToDoItem;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeCell extends TableCell<ToDoItem, LocalDateTime> {

    @Override
    protected void updateItem(LocalDateTime item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
            setText(null);
        } else {
            setText(item.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
        }
    }
}
