package seng302.Model;


import com.google.gson.annotations.SerializedName;

public enum OrganDeregisterReason {
    @SerializedName("Registration error")
    REGISTRATION_ERROR("Registration error"),
    @SerializedName("Disease cured")
    DISEASE_CURED("Disease cured"),
    @SerializedName("Receiver died")
    RECEIVER_DIED("Receiver died"),
    @SerializedName("Transplant received")
    TRANSPLANT_RECEIVED("Transplant received");

    public String deregisterReason;

    OrganDeregisterReason(String deregisterReason) {
        this.deregisterReason = deregisterReason;
    }

    @Override
    public String toString() {
        return deregisterReason;
    }
}
