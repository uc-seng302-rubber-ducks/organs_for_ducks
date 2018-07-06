package odms.security;

import com.google.gson.annotations.SerializedName;
import org.springframework.security.core.GrantedAuthority;

public enum UserRole implements GrantedAuthority {
    @SerializedName("admin")
    ADMIN,
    @SerializedName("clinician")
    CLINICIAN;

    @Override
    public String getAuthority() {
        return name();
    }
}
