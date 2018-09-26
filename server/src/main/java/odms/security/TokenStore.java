package odms.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * Spring component to store all the known user tokens in a session. Known tokens are forgotten when the server is restarted.
 * The database is never aware of these tokens
 */
@Component
public class TokenStore {

    private Set<AuthToken> knownAuthTokens;

    @Autowired
    public TokenStore() {
        knownAuthTokens = new HashSet<>();
    }

    /**
     * Checks to see if a valid token is requested
     * <p>
     * A timed out token will return the same as a token that does not exist
     * and will be removed from the set of known tokens
     *
     * @param token token to look for
     * @return Token for the
     */
    AuthToken get(String token) {
        for (AuthToken t : knownAuthTokens) {
            if (t.getToken().equals(token)) {
                return t;
            }
        }
        return null;
    }

    public boolean add(AuthToken authToken) {
        return knownAuthTokens.add(authToken);
    }

    public boolean contains(AuthToken authToken) {
        return knownAuthTokens.contains(authToken);
    }

    boolean contains(String tokenStr) {
        return get(tokenStr) != null;
    }

    public boolean remove(AuthToken authToken) {
        return knownAuthTokens.remove(authToken);
    }

    public boolean remove(String tokenStr) {
        AuthToken t = get(tokenStr);
        return knownAuthTokens.remove(t);
    }

}
