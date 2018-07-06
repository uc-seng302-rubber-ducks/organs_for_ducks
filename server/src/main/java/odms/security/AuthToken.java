package odms.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.security.SecureRandom;
import java.util.Collections;

/**
 * Class for storing and generating tokens to allow secure access to the application
 */
public class AuthToken extends AbstractAuthenticationToken {

    private static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int TOKEN_LENGTH = 32;
    private static SecureRandom rand = new SecureRandom();
    private final String token;
    private String userId;
    private String userType;


    public AuthToken(String userId, String userType, String token) {
        super(Collections.singletonList(UserRole.valueOf(userType)));
        this.token = token;
        this.userId = userId;
        this.userType = userType;
        setAuthenticated(true);
    }

    public AuthToken(String token) {
        super(null);
        this.token = token;
        setAuthenticated(false);
    }

    /**
     * Generates a random unique token
     *
     * @return A token of our appreciation
     */
    public static String generateToken() {
        StringBuilder sb = new StringBuilder(TOKEN_LENGTH);
        for (int i = 0; i < TOKEN_LENGTH; i++) {
            sb.append(AB.charAt(rand.nextInt(AB.length() - 1)));
        }
        return sb.toString();
    }

    public String getToken() {
        return token;
    }

    public String getUserType() {
        return userType;
    }

    public String getUserId() {
        return userId;
    }


    @Override
    public Object getCredentials() {
        return token;
    }

    @Override
    public Object getPrincipal() {
        return userId;
    }
}
