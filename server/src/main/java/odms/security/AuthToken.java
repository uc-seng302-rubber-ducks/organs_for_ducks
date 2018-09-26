package odms.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.security.SecureRandom;
import java.time.LocalDateTime;

/**
 * Class for storing and generating tokens to allow secure access to the application
 */
public class AuthToken extends AbstractAuthenticationToken {

    private static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int TOKEN_LENGTH = 32;
    private static final long TOKEN_TTL = 30; // token lifetime in minutes
    private static SecureRandom rand = new SecureRandom();
    private final String token;
    private String userId;
    private String userType;
    private LocalDateTime expiryTime;


    /**
     * valid AuthToken constructed when all details are given. note
     * setAuthenticated(true)
     *
     * @param userId   username/id of the user
     * @param userType the type/role of the user. The user will be authenticated with the privileges listed here
     * @param token    the token that has been provided
     * @see UserRole
     */
    public AuthToken(String userId, String userType, String token) {
        super((UserRole.getHeirarchicalAuth(UserRole.valueOf(userType))));
        this.token = token;
        this.userId = userId;
        this.userType = userType;
        LocalDateTime now = LocalDateTime.now();
        this.expiryTime = now.plusMinutes(TOKEN_TTL);
        setAuthenticated(true);
    }

    /**
     * creates a bad token on bad login
     *
     * @param token bad token passed in
     */
    public AuthToken(String token) {
        super(null);
        this.token = token;
        LocalDateTime now = LocalDateTime.now();
        this.expiryTime = now.plusMinutes(TOKEN_TTL);
        setAuthenticated(false);
    }

    /**
     * Generates a random unique token
     * <p>
     * System.out.println("Password hash " + passwordHash);
     * System.out.println("expected " + expectedHash);
     * System.out.println(passwordHash.equals(expectedHash)); *
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

    void renew() {
        LocalDateTime now = LocalDateTime.now();
        this.expiryTime = now.plusMinutes(TOKEN_TTL);
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


    public boolean isTokenAlive() {
        return LocalDateTime.now().isBefore(expiryTime);
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
