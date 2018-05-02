package seng302.Model;

import java.time.LocalDate;

/**
 * stores the details of a receiver organ
 * of a receiver's organ.
 */
public class ReceiverOrganDetails {

    private LocalDate registerDate;

    private LocalDate deRegisterDate;

    public ReceiverOrganDetails(){

    }

    public LocalDate getDeRegisterDate() {
        return deRegisterDate;
    }

    public LocalDate getRegisterDate() {
        return registerDate;
    }

    public void setDeRegisterDate(LocalDate deRegisterDate) {
        this.deRegisterDate = deRegisterDate;
    }

    public void setRegisterDate(LocalDate registerDate) {
        this.registerDate = registerDate;
    }

    @Override
    public String toString() {
        return "ReceiverOrganDetails{" +
                "registerDate=" + registerDate +
                ", deRegisterDate=" + deRegisterDate +
                '}';
    }
}
