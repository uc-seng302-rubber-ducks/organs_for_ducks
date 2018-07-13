package odms.security;

import odms.commons.model.CacheManager;
import odms.commons.model.datamodel.TimedCacheValue;
import odms.commons.security.TokenCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Spring component to store all the known user tokens in a session. Known tokens are forgotten when the server is restarted.
 * The database is never aware of these tokens
 */
@Component
public class TokenStore {

    private Set<AuthToken> knownAuthTokens;
    private TokenCache tokenCache;

    @Autowired
    public TokenStore() {
        knownAuthTokens = new HashSet<>();
        tokenCache = CacheManager.getInstance().getTokenCache();
    }

    /**
     * Checks to see if a valid token is requested
     *
     * A timed out token will return the same as a token that does not exist
     * and will be removed from the set of known tokens
     *
     * @param token token to look for
     * @return
     */
    AuthToken get(String token) {

        tokenCache.removeOlderThan(LocalDateTime.now());
        if (tokenCache.get(token) == null){
            remove(token);
            return null;
        }
        for (AuthToken t : knownAuthTokens) {
            if (t.getToken().equals(token)){
                return t;
            }
        }
        return null;
    }

    public boolean add(AuthToken authToken) {
        if(!tokenCache.containsKey(authToken.getToken())) {
            tokenCache.add(authToken.getToken(), new TimedCacheValue<>("Unsure on the implementation of this"));
        }
        return knownAuthTokens.add(authToken);
    }

    public boolean contains(AuthToken authToken) {
        return knownAuthTokens.contains(authToken);
    }

    boolean contains(String tokenStr) {
        return get(tokenStr) != null;
    }

    public boolean remove(AuthToken authToken) {
        tokenCache.evict(authToken.getToken());
        return knownAuthTokens.remove(authToken);
    }

    public boolean remove(String tokenStr) {
        tokenCache.evict(tokenStr);
        AuthToken t = get(tokenStr);
        return knownAuthTokens.remove(t);
    }

}
