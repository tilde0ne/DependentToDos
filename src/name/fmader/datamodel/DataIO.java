package name.fmader.datamodel;

import name.fmader.common.Settings;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DataIO {

    private static final DataIO instance = new DataIO();

    private List<ToDoItem> toDoItems = new ArrayList<>();
    private List<String> contexts = new ArrayList<>();
    private Settings settings = new Settings();

    private String path = Settings.DEFAULT_PATH;
    private File dataFile = Settings.DEFAULT_FILE;
    private File settingsFile = Settings.SETTINGS_FILE;

    public boolean loadSettings() {
        if (settingsFile.exists()) {
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(settingsFile))) {
                settings = (Settings) in.readObject();
                path = settings.getCustomPath();
                dataFile = settings.getLastFile();

                return true;
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            saveSettings();
            return false;
        }
    }

    public boolean load() {
        return load(dataFile);
    }

    public boolean load(File file) {
        if (file.exists()) {
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
                toDoItems = (ArrayList<ToDoItem>) in.readObject();
                contexts = (ArrayList<String>) in.readObject();

                dataFile = file;
                settings.setLastFile(file);

                return true;
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            save();
            return false;
        }
    }

    public boolean saveSettings() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(settingsFile))) {
            out.writeObject(settings);

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean save() {
        return save(dataFile);
    }

    public boolean save(File file) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(toDoItems);
            out.writeObject(contexts);

            dataFile = file;
            settings.setLastFile(file);

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
    }

    public File getDataFile() {
        return dataFile;
    }

    public void setDataFile(File dataFile) {
        this.dataFile = dataFile;
        settings.setLastFile(dataFile);
    }

    public Settings getSettings() {
        return settings;
    }
}
