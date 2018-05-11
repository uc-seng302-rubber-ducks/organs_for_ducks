package seng302.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import seng302.Directory;

public class Log {

  private Logger LOGGER;
  private Class sourceClass;
  //a single timestamp created when the app launches
  private static long bootTime = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);

  /**
   * Creates a new logger object. The log files created are placed in the LOGS directory alongside
   * the JSON folder. The naming convention is unixtimestamp
   */
  public Log(Class sourceClass) {
    this.LOGGER = Logger.getLogger(sourceClass.getName());
    try {
      Files.createDirectories(Paths.get(Directory.LOGS.directory()));
      Handler handler = new FileHandler(Directory.LOGS
          .directory() + bootTime + sourceClass.getSimpleName() + ".log", true);
      LOGGER.addHandler(handler);
    } catch (IOException ex) {
      LOGGER.log(Level.WARNING, "failed to set up logging to file", ex);
    }

    this.sourceClass = sourceClass;
  }


  /**
   * Logging method for simple info strings
   *
   * @param message string message to be logged
   */
  public void info(String message) {
    LOGGER.log(Level.INFO, message, sourceClass);
  }


  public void warn(String message) {
    LOGGER.log(Level.WARNING, message, sourceClass);
  }

  public void warn(String message, Throwable e) {
    LOGGER.log(Level.WARNING, message, e);
  }


  public void severe(String message, Throwable e) {
    LOGGER.log(Level.SEVERE, message, e);
  }

}
