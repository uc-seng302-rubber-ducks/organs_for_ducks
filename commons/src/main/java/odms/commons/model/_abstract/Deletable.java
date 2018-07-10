package odms.commons.model._abstract;

/**
 * Abstract Class for all objects that can be deleted.
 */
public abstract class Deletable {
    private boolean deleted = false;

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
