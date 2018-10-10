package odms.controller.gui.widget;

/**
 * Interface for all loading widgets. Each loading widget must have the 
 * setWaiting method to indicate whether or not is it waiting for data.
 */
public interface CountableLoadingWidget {
    void setWaiting(boolean waiting);

    void setCount(int value);

    int getCount();
}
