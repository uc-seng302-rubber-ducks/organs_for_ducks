package odms.controller.gui.widget;

/**
 * Interface for all loading widgets. Each loading widget must have the 
 * setWaiting method to indicate whether or not is it waiting for data.
 */
public interface LoadingWidget {
    void setWaiting(boolean waiting);
}
