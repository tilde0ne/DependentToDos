package name.fmader.common;

import java.io.File;
import java.io.Serializable;

public class Settings implements Serializable {

    public static final String DEFAULT_FILENAME = "data.dtd";
    public static final String DEFAULT_PATH = System.getProperty("user.home");
    public static final File settingsFile = new File(DEFAULT_PATH + File.separator + "settings.bin");

    private String customPath = DEFAULT_PATH;
    private File lastFile = null;

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
