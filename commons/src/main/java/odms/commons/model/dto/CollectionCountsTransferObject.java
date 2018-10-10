package odms.commons.model.dto;

import java.util.Collection;

public class CollectionCountsTransferObject<T> {
    private Collection<T> collection;
    private int totalCount;

    public CollectionCountsTransferObject(Collection<T> collection, int count) {
        this.collection = collection;
        this.totalCount = count;
    }

    public Collection<T> getCollection() {
        return collection;
    }

    public void setCollection(Collection<T> collection) {
        this.collection = collection;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
