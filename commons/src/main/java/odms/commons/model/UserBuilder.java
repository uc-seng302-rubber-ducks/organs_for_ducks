package odms.commons.model;

import odms.commons.model._abstract.IgnoreForUndo;
import odms.commons.model.dto.UserOverview;

/**
 * builder class for user utilising the
 *
 * @see IgnoreForUndo annotation
 * any changes by this builder will not be stored as undoable actions, (ideally) saving on memory
 */
@IgnoreForUndo
public class UserBuilder {


    private User secret;

    public UserBuilder() {
        secret = new User();
    }

    public UserBuilder(UserOverview basedOn) {
        secret = UserOverview.toUser(basedOn);

    }

    public UserBuilder setNhi(String nhi) {
        secret.setNhi(nhi);
        return this;
    }

    public UserBuilder setFirstName(String name) {
        secret.setFirstName(name);
        return this;
    }

    public User build() {
        return secret;
    }
}
