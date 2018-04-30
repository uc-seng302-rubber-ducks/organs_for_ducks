package seng302.Model;

import java.time.LocalDate;

/**
 * stores the details of a receiver organ
 * of a receiver's organ.
 */
public class ReceiverOrganDetails {

    private LocalDate registerDate;

    private LocalDate deRegisterDare;

    public ReceiverOrganDetails(){

    }

    public LocalDate getDeRegisterDare() {
        return deRegisterDare;
    }

    public LocalDate getRegisterDate() {
        return registerDate;
    }

    public void setDeRegisterDare(LocalDate deRegisterDare) {
        this.deRegisterDare = deRegisterDare;
    }

    public void setRegisterDate(LocalDate registerDate) {
        this.registerDate = registerDate;
    }
}
