package odms.model._abstract;

import odms.model.User;

/**
 * Interface that any controller that uses the transplant waiting list MUST implement.
 */
public interface TransplantWaitListViewer {

    /**
     * Implementation of this should open a new user overview controller object, loading from the userView.fxml
     *
     * @param user The user to open
     */
    void launchUser(User user);
}
