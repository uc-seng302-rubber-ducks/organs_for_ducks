package seng302.Model;


import com.google.gson.annotations.Expose;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Class for instantiating a medical procedure
 * @author Josh Burt
 */
public class MedicalProcedure {

    @Expose
    private LocalDate procedureDate;
    @Expose
    private String summary;
    @Expose
    private String description;
    @Expose
    private ArrayList<Organs> organsAffected;

    public MedicalProcedure(LocalDate procedureDate, String summary, String description, ArrayList<Organs> organsAffected) {

        this.procedureDate = procedureDate;
        this.summary = summary;
        this.description = description;
        this.organsAffected = organsAffected;
    }

    public MedicalProcedure(LocalDate procedureDate, String summary) {
        this.procedureDate = procedureDate;
        this.summary = summary;
    }

    public MedicalProcedure() {
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

    public ArrayList<Organs> getOrgansAffected() {
        return organsAffected;
    }

    public void setOrgansAffected(ArrayList<Organs> organsAffected) {
        this.organsAffected = organsAffected;
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
}
