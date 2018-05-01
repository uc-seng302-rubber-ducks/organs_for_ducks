package seng302.Model;

import com.google.gson.annotations.Expose;

import java.util.HashSet;

public class OrganIntersection {
    @Expose
    private HashSet<Organs> intersection = new HashSet<>();
    @Expose
    private transient User attachedUser;


    public OrganIntersection(User user) {
        attachedUser = user;
    }

    public HashSet<Organs> getIntersection() {
        return intersection;
    }

    public void addOrganIntersection(Organs organ) {
        intersection.add(organ);
    }

    public void removeOrganIntersection(Organs organ) {
        intersection.remove(organ);
    }

    public boolean organIsPresent(Organs organ) {
        return intersection.contains(organ);
    }

    public boolean intersectionIsEmpty() {
        return intersection.isEmpty();
    }
}
