package odms.security;

import com.google.gson.annotations.SerializedName;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.List;

public enum UserRole implements GrantedAuthority {
    @SerializedName("admin")
    ADMIN,
    @SerializedName("clinician")
    CLINICIAN,
    @SerializedName("user")
    USER;

    @Override
    public String getAuthority() {
        return name();
    }

    /**
     * Allows admins to have clinician access without explicitly listing it
     *
     * @param role String containing one of the roles listed in this enum
     * @return list of roles given to that user.
     * <p>
     * yes IntelliJ, heirarchical is a word.
     */
    public static List<UserRole> getHeirarchicalAuth(UserRole role) {
        List<UserRole> roles = new ArrayList<>();
        if (role.equals(ADMIN)) {
            roles.add(ADMIN);
            roles.add(CLINICIAN);
            roles.add(USER);
        }
        if (role.equals(CLINICIAN)) {
            roles.add(CLINICIAN);
            roles.add(USER);
        }
        if (role.equals(USER)) {
            roles.add(USER);
        }

        if (roles.isEmpty()) {
            return null;
        }
        return roles;
    }
}
