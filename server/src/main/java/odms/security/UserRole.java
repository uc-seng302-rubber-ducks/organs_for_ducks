package odms.security;

import org.springframework.security.core.GrantedAuthority;

public enum UserRole implements GrantedAuthority {
    ADMIN,
    CLINICIAN;

    @Override
    public String getAuthority() {
        return name();
    }
}
