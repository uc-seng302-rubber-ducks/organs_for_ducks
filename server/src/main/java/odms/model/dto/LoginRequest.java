package odms.model.dto;

import odms.security.UserRole;

/**
 * POJO to contain a login request
 * needs to be in the server project as it relies on UserRole, a server only enum
 */
public class LoginRequest {
    String username;
    UserRole role;
    String password;

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
