package odms.model.datamodel;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class MedicationInteractionsResponse {
    @SerializedName("age_interaction")
    private Map<String, List<String>> ageInteractions;

    @SerializedName("co_existing_conditions")
    private Map<String, Integer> coExistingConditions;

    @SerializedName("duration_interaction")
    private Map<String, List<String>> durationInteractions;

    @SerializedName("gender_interaction")
    private Map<String, List<String>> genderInteractions;

    @Override
    public String toString() {
        return "MedicationInteractionsResponse{" +
                "ageInteractions=" + ageInteractions +
                ", coExistingConditions=" + coExistingConditions +
                ", durationInteractions=" + durationInteractions +
                ", genderInteractions=" + genderInteractions +
                '}';
    }

    public Map<String, List<String>> getAgeInteractions() {
        return ageInteractions;
    }

    public Map<String, Integer> getCoExistingConditions() {
        return coExistingConditions;
    }

    public Map<String, List<String>> getDurationInteractions() {
        return durationInteractions;
    }

    public Map<String, List<String>> getGenderInteractions() {
        return genderInteractions;
    }
}
