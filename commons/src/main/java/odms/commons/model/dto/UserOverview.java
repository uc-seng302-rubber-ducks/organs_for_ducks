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
        return overview;
    }

    public static User toUser(UserOverview overview) {
        //TODO implement me
        throw new NullPointerException();
    }

    public void setNhi(String nhi) {
        this.nhi = nhi;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public void setDod(LocalDate dod) {
        this.dod = dod;
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

    public void setDonating(Set<Organs> donating) {
        this.donating = donating;
    }

    public void setReceiving(Map<Organs, ArrayList<ReceiverOrganDetailsHolder>> receiving) {
        this.receiving = receiving;
    }
}
