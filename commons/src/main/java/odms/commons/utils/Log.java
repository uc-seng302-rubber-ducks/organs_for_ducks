package odms.commons.utils;

import odms.commons.model._enum.Directory;
import okhttp3.OkHttpClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.logging.*;

/**
 * static logger class This is essentially a static wrapper for the java.utils.logger. Use info for
 * general information. e.g. "opening __Controller", "closing __ window", etc. Use warning for minor
 * errors or things that can be recovered from. e.g. "__ api returned 404", "could not find file
 * __", etc. Use severe for major, app-crashing faults. These should always have an exception e.g.
 * "could not load login scene"
 */
public class Log {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger("ODMS");
    private static long bootTime = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);

    private Log() {
    }

    /**
     * Sets up the Logger to start logging stuff
     */
    public static void setup(boolean server) {
        Logger logger = Logger.getLogger("ODMS");
        Handler handler;
        try {
            //creates file/path if it doesn't already exist
            Files.createDirectories(Paths.get(Directory.SERVER_LOGS.directory()));
            Logger.getLogger(OkHttpClient.class.getName()).setLevel(Level.FINE);
            Files.createDirectories(Paths.get(Directory.CLIENT_LOGS.directory()));
            if (server) {
                handler = new FileHandler(Directory.SERVER_LOGS
                        .directory() + bootTime + ".log", true);
            } else {
                handler = new FileHandler(Directory.CLIENT_LOGS
                        .directory() + bootTime + ".log", true);
            }

            SimpleFormatter formatter = new SimpleFormatter();
            handler.setFormatter(formatter);
            logger.addHandler(handler);

            //disables logging to console
            logger.setUseParentHandlers(server);

        } catch (IOException ex) {
            logger.log(Level.WARNING, "failed to set up logging to file", ex);
        } catch (SecurityException ex) {
            logger.log(Level.SEVERE, "failed to set up logging to file", ex);
        }
    }

    /**
     * Creates a log entry at the INFO level. If there is an exception to be logged, please escalate
     * to warning
     *
     * @param message String message to be logged
     */
    public static void info(String message) {
        logger.log(Level.INFO, message);
    }


    /**
     * Creates a log entry at the WARNING level.
     *
     * @param message String message to be logged
     */
    public static void warning(String message) {
        logger.log(Level.WARNING, message);
    }

    /**
     * Creates a log entry at the WARNING level.
     *
     * @param message String message to be logged
     * @param ex      Throwable/exception that has been caught
     */
    public static void warning(String message, Throwable ex) {
        logger.log(Level.WARNING, message, ex);
    }

    /**
     * Creates a log entry at the SEVERE level. This should only be used for app-crashing errors
     *
     * @param message String message to be logged
     * @param ex      Throwable/exception that has been caught
     */
    public static void severe(String message, Throwable ex) {
        logger.log(Level.SEVERE, message, ex);
    }
}
