package name.fmader.datamodel;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DataIO {

    public static final DataIO instance = new DataIO();

    private List<ToDoItem> toDoItems;
    private List<String> contexts;

    private DataIO() {
        this.contexts = new ArrayList<>();
        this.toDoItems = new ArrayList<>();
    }

    public boolean load() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("data.dtd"))) {
            toDoItems = (ArrayList<ToDoItem>) in.readObject();
            contexts = (ArrayList<String>) in.readObject();

            return true;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean save() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("data.dtd"))) {
            out.writeObject(toDoItems);
            out.writeObject(contexts);

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Getters
    //////////

    public List<ToDoItem> getToDoItems() {
        return toDoItems;
    }

    public List<String> getContexts() {
        return contexts;
    }

    public static DataIO getInstance() {
        return instance;
    }
}
