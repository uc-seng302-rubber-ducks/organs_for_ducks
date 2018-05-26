package seng302;

/**
 * Declares directories and file setup information in one place so it can be referred to later
 *
 * @author Josh Burt
 */
public enum Directory {

    ROOT(System.getProperty("user.home") + "/.organs/"),
    JSON(ROOT.directory() + "JSON/"),
    LOGS(ROOT.directory() + "LOGS/");

    private String directory;

    Directory(String directory) {
        this.directory = directory;
    }

    public String directory() {
        return directory;
    }

}
