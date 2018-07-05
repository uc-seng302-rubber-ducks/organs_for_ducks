package odms.model.response;

import odms.commons.model._enum.Organs;
import odms.model.datamodel.Name;


import java.time.LocalDate;
import java.util.List;

/**
 * simple sub-object of user containing only the relevant fields for overview in the client
 */
public class UserOverview {

    private String nhi;
    private LocalDate dob;
    private  LocalDate dod;
    private Name name;
    private List<Organs> donating;
    private List<Organs> receiving;

    public void setNhi(String nhi) {
        this.nhi = nhi;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public void setDod(LocalDate dod) {
        this.dod = dod;
    }

    public void setName(Name name) {
        this.name = name;
    }

    public void setDonating(List<Organs> donating) {
        this.donating = donating;
    }

    public void setReceiving(List<Organs> receiving) {
        this.receiving = receiving;
    }
}
