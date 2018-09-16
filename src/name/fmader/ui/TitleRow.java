package name.fmader.ui;

import javafx.scene.control.TableRow;
import name.fmader.datamodel.ToDoItem;

import java.time.LocalDate;

public class TitleRow extends TableRow<ToDoItem> {
    @Override
    protected void updateItem(ToDoItem item, boolean empty) {
        super.updateItem(item, empty);

        if (item == null || empty) {
            setStyle("");
        } else {
            if ((item.getStart() != null && item.getStart().isAfter(LocalDate.now())) || item.isIsDependent()) {
                setStyle("-fx-background-color: lightgray");
            } else {
                setStyle("-fx-background-color: darkseagreen");
            }
        }
    }
}
