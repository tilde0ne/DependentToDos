package name.fmader.datamodel;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DataIO {

    private static final DataIO instance = new DataIO();

    private List<ToDoItem> toDoItems;
    private List<String> contexts;

    private String path = System.getProperty("user.home") + File.separator + "data.dtd";
    private File data = new File(path);

    private DataIO() {
        this.contexts = new ArrayList<>();
        this.toDoItems = new ArrayList<>();
    }

    public boolean load() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(data))) {
            toDoItems = (ArrayList<ToDoItem>) in.readObject();
            contexts = (ArrayList<String>) in.readObject();

            return true;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean save() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(data))) {
            out.writeObject(toDoItems);
            out.writeObject(contexts);

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean backup() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("backup.dtd"))) {
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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
        data = new File(path);
    }
}
