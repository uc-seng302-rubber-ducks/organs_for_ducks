package seng302.Model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * simple interface to enforce the use of PropertChangeEvents for classes that are intended to be
 * listened to
 */
public interface Listenable {

  /**
   * wrapper of java.beans.PropertyChangeSupport.addPropertyChangeListener
   *
   * @param listener listener to be added
   */
  void addPropertyChangeListener(PropertyChangeListener listener);

  /**
   * wrapper of java.beans.PropertyChangeSupport.removePropertyChangeListener
   *
   * @param listener listener to be removed
   */
  void removePropertyChangeListener(PropertyChangeListener listener);

  /**
   * wrapper of java.beans.PropertyChangeSupport.fire
   *
   * @param event PropertyChangeEvent to be fired
   */
  void fire(PropertyChangeEvent event);
}
