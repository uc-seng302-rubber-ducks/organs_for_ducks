package odms.commons.model.dto;

import odms.commons.model.User;
import odms.commons.model._enum.Organs;
import odms.commons.model.datamodel.ReceiverOrganDetailsHolder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
 * simple sub-object of user containing only the relevant fields for overview in the client
 */
public class UserOverview {

    private String nhi;
    private LocalDate dob;
    private LocalDate dod;
    private Name name;
    private String region;
    private Set<Organs> donating;
    private Map<Organs, ArrayList<ReceiverOrganDetailsHolder>> receiving;

    /**
     * conversion method to take whole user down to overview for smaller packet size
     *
     * @param user user to convert to overview
     * @return compressed version of the user given
     */
    public static UserOverview fromUser(User user) {
        UserOverview overview = new UserOverview();
        overview.setDob(user.getDateOfBirth());
        overview.setDod(user.getDateOfDeath());
        overview.setName(new Name(user.getFirstName(), user.getMiddleName(), user.getLastName()));
        overview.setNhi(user.getNhi());
        overview.setDonating(user.getDonorDetails().getOrgans());
        overview.setReceiving(user.getReceiverDetails().getOrgans());
        overview.setRegion(user.getRegion());
        return overview;
    }

    public static User toUser(UserOverview overview) {
        //TODO implement me
        throw new NullPointerException();
    }

    public String getNhi() {
        return nhi;
    }

    public void setNhi(String nhi) {
        this.nhi = nhi;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public LocalDate getDod() {
        return dod;
    }

    public void setDod(LocalDate dod) {
        this.dod = dod;
    }

    public String getFirstName() {
        return name.getFirstName();
    }

    public String getMiddleName() {
        return name.getMiddleNames();
    }

    public String getLastName() {
        return name.getLastName();
    }

    public Set<Organs> getDonating() {
        return donating;
    }

    public void setDonating(Set<Organs> donating) {
        this.donating = donating;
    }

    public boolean isDonor() {
        return !donating.isEmpty();
    }

    public boolean isReceiver() {
        return !receiving.isEmpty();
    }

    public Map<Organs, ArrayList<ReceiverOrganDetailsHolder>> getReceiving() {
        return receiving;
    }

    public void setReceiving(Map<Organs, ArrayList<ReceiverOrganDetailsHolder>> receiving) {
        this.receiving = receiving;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    @Override
    public int hashCode() {
        return nhi.hashCode();
    }

    //based on NHI only to keep in line with User
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof UserOverview)) {
            return false;
        }
        return nhi.equals(((UserOverview) obj).nhi);
    }

    public void setName(Name name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "UserOverview{" +
                "nhi='" + nhi + '\'' +
                ", dob=" + dob +
                ", dod=" + dod +
                ", name=" + name.toString() +
                ", region='" + region + '\'' +
                ", donating=" + donating +
                ", receiving=" + receiving +
                '}';
    }
}
