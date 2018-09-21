package name.fmader.common;

import java.io.File;
import java.io.Serializable;

public class Settings implements Serializable {

    private static final long serialVersionUID = 6104340902959813208L;

    public static final String DEFAULT_FILENAME = "data.dtd";
    public static final String DEFAULT_PATH = System.getProperty("user.home");
    public static final File DEFAULT_FILE = new File(DEFAULT_PATH + File.separator + DEFAULT_FILENAME);
    public static final File SETTINGS_FILE = new File(DEFAULT_PATH + File.separator + "dtd_settings.bin");

    private String customPath = DEFAULT_PATH;
    private File lastFile = DEFAULT_FILE;

    public String getCustomPath() {
        return customPath;
    }

    public void setCustomPath(String customPath) {
        this.customPath = customPath;
    }

    public File getLastFile() {
        return lastFile;
    }

    public void setLastFile(File lastFile) {
        this.lastFile = lastFile;
    }
}
