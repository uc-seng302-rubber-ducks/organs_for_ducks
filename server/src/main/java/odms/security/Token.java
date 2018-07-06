package odms.security;

import odms.commons.model.User;
import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * Class for storing and generating tokens to allow secure access to the application
 */
public class Token extends AbstractAuthenticationToken{

    private static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int TOKEN_LENGTH = 32;
    private static SecureRandom rand;
    private final String token;
    private String userId;
    private String userType;


    public Token(String userId, String userType){
        super(Collections.singletonList(UserRole.valueOf(userType)));
        token = generateToken();
        this.userId = userId;
        this.userType = userType;
        setAuthenticated(true);
    }

    public Token(String token){
        super(null);
        this.token = token;
        setAuthenticated(false);
    }

    public String getToken() {
        return token;
    }

    /**
     * Generates a random unique token
     *
     * @return A token of our appreciation
     */
    private String generateToken() {
        StringBuilder sb = new StringBuilder(TOKEN_LENGTH);
        for (int i = 0; i < TOKEN_LENGTH; i++) {
            sb.append(AB.charAt(rand.nextInt(AB.length())));
        }
        return sb.toString();
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
