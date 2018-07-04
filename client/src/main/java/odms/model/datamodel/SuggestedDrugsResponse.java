package odms.model.datamodel;

import com.google.gson.annotations.SerializedName;

import java.util.Set;

public class SuggestedDrugsResponse {
    @SerializedName("suggestions")
    private Set<String> suggestions;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String s : suggestions) {
            sb.append(s + ",");
        }
        return sb.toString();
    }

    public Set<String> getSuggestions() {
        return suggestions;
    }
}