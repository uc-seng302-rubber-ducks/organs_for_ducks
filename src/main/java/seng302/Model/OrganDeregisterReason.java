package seng302.Model;

import com.google.gson.annotations.SerializedName;

public enum OrganDeregisterReason {
    @SerializedName("Registration error")
    REGISTRATION_ERROR("Registration Error"),
    @SerializedName("Disease cured")
    DISEASE_CURED("Disease cured"),
    @SerializedName("Receiver died")
    RECEIVER_DIED("Receiver died");

    public String reasonName;

    OrganDeregisterReason(String reasonName) {this.reasonName = reasonName; }

    @Override
    public String toString() { return reasonName; }
}
