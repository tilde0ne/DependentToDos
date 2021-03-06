package name.fmader.ui;

import javafx.scene.control.TableCell;
import name.fmader.datamodel.ToDoItem;

public class TitleCell extends TableCell<ToDoItem, String> {
    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);

        if (item == null || empty) {
            setText(null);
        } else {
            int row = getIndex();
            ToDoItem toDoItem = getTableView().getItems().get(row);
            if (toDoItem.getInherited()) {
                item = "[!] " + item;
            }
            setText(item);
        }
        getStyleClass().add("title");
    }
}
