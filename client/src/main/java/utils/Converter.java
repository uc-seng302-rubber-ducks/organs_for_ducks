package utils;

/**
 * Interface to make sure that any item that converts Controls has a startEdit and stopEdit method
 */
public interface Converter {
    /**
     * Start the editing of a control, should trigger conversion from an uneditable JavaFX Control to a user editable one
     */
    void startEdit();

    /**
     * Stop the editing of a control, should trigger converison from an editable JavaFX Control to a user editable one
     */
    void stopEdit();
}
