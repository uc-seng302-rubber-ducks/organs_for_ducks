package odms.commons.model;


import com.google.gson.annotations.Expose;
import odms.commons.model._abstract.Deletable;
import odms.commons.model._enum.Organs;
import odms.commons.model.datamodel.ProcedureKey;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for instantiating a medical procedure
 *
 * @author Josh Burt
 */
public class MedicalProcedure extends Deletable {

    @Expose
    private LocalDate procedureDate;
    @Expose
    private String summary;
    @Expose
    private String description;
    @Expose
    private List<Organs> organsAffected;

    /**
     * Constructor for Medical Procedure
     *
     * @param procedureDate  date of procedure
     * @param summary        summary of procedure
     * @param description    description of procedure
     * @param organsAffected affected organs
     */
    public MedicalProcedure(LocalDate procedureDate, String summary, String description, ArrayList<Organs> organsAffected) {

        this.procedureDate = procedureDate;
        this.summary = summary;
        this.description = description;
        if (organsAffected == null) {
            this.organsAffected = new ArrayList<>();
        } else {
            this.organsAffected = organsAffected;
        }
    }

    /**
     * Constructor for Medical Procedure
     */
    MedicalProcedure() {
        organsAffected = new ArrayList<>();
    }

    public LocalDate getProcedureDate() {
        return procedureDate;
    }

    public void setProcedureDate(LocalDate procedureDate) {
        this.procedureDate = procedureDate;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Organs> getOrgansAffected() {
        return organsAffected;
    }

    public void setOrgansAffected(ArrayList<Organs> organsAffected) {
        this.organsAffected = organsAffected;
    }

    public void addOrgan(Organs organ) {
        organsAffected.add(organ);
    }

    public void removeOrgan(Organs organ) {
        organsAffected.remove(organ);
    }

    @Override
    public String toString() {
        return "MedicalProcedure{" +
                "procedureDate=" + procedureDate +
                ", summary='" + summary + '\'' +
                ", description='" + description + '\'' +
                ", organsAffected=" + organsAffected +
                '}';
    }

    /**
     * Generates a procedure key to easily identify a medical procedure
     *
     * @return A ProcedureKey object containing the name and date of the procedure.
     */
    public ProcedureKey getKey() {
        return new ProcedureKey(summary, procedureDate);
    }
}
