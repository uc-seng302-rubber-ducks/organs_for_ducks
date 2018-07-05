package odms.commons.utils;

import java.util.logging.Level;

/**
 * static logger class This is essentially a static wrapper for the java.utils.logger. Use info for
 * general information. e.g. "opening __Controller", "closing __ window", etc. Use warning for minor
 * errors or things that can be recovered from. e.g. "__ api returned 404", "could not find file
 * __", etc. Use severe for major, app-crashing faults. These should always have an exception e.g.
 * "could not load login scene"
 */
public class Log {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger("ODMS");

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
