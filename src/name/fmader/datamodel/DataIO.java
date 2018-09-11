package name.fmader.datamodel;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DataIO {

    public static final DataIO instance = new DataIO();

    private List<ToDoItem> toDoItems;
    private List<Appointment> appointments;
    private List<External> externals;
    private List<Project> projects;
    private List<String> contexts;

    private DataIO() {
        this.toDoItems = new ArrayList<>();
        this.appointments = new ArrayList<>();
        this.externals = new ArrayList<>();
        this.projects = new ArrayList<>();
        this.contexts = new ArrayList<>();
    }

    public boolean load() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("data.dtd"))) {
            toDoItems = (ArrayList<ToDoItem>) in.readObject();
            appointments = (ArrayList<Appointment>) in.readObject();
            externals = (ArrayList<External>) in.readObject();
            projects = (ArrayList<Project>) in.readObject();
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
            out.writeObject(appointments);
            out.writeObject(externals);
            out.writeObject(projects);
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

    public List<Appointment> getAppointments() {
        return appointments;
    }

    public List<External> getExternals() {
        return externals;
    }

    public List<Project> getProjects() {
        return projects;
    }

    public List<String> getContexts() {
        return contexts;
    }

    public static DataIO getInstance() {
        return instance;
    }
}
