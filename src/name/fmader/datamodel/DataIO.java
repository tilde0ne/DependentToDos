package name.fmader.datamodel;

import java.util.ArrayList;
import java.util.List;

public class DataIO {

    public static final DataIO instance = new DataIO();

    private final List<ToDoItem> toDoItems;
    private final List<Appointment> appointments;
    private final List<External> externals;
    private final List<Project> projects;
    private final List<String> contexts;

    private DataIO() {
        this.toDoItems = new ArrayList<>();
        this.appointments = new ArrayList<>();
        this.externals = new ArrayList<>();
        this.projects = new ArrayList<>();
        this.contexts = new ArrayList<>();
    }

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
