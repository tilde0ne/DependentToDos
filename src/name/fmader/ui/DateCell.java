package name.fmader.ui;

import javafx.scene.control.TableCell;
import javafx.scene.paint.Color;
import name.fmader.datamodel.ToDoItem;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateCell extends TableCell<ToDoItem, LocalDate> {

    @Override
    protected void updateItem(LocalDate item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
            setText(null);
            setStyle("");
        } else {
            setText(item.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            if (item.isBefore(LocalDate.now().plusDays(1))) {
                setStyle("-fx-background-color: red");
                setTextFill(Color.WHITE);
            } else if (item.isBefore(LocalDate.now().plusDays(4))) {
                setStyle("-fx-background-color: orange");
                setTextFill(Color.BLACK);
            } else if (item.isBefore(LocalDate.now().plusDays(8))) {
                setStyle("-fx-background-color: yellow");
                setTextFill(Color.BLACK);
            } else {
                setStyle("-fx-background-color: white");
                setTextFill(Color.BLACK);
            }
        }
    }
}
