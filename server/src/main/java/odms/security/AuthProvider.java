package odms.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 * provides custom authentication for the server.
 *
 * @see AuthFilter
 * @see TokenStore
 */
public class AuthProvider implements AuthenticationProvider {

    @Autowired
    TokenStore store;

    @Override
    public Authentication authenticate(Authentication auth) throws AuthenticationException {
        final AuthToken authTokenContainer = (AuthToken) auth;
        final String token = authTokenContainer.getToken();

        if (!store.contains(token) || !store.get(token).isTokenAlive()) {
            throw new BadCredentialsException("Invalid token: " + token);
        } else {
            store.get(token).renew();
        }

        return store.get(token);
    }

    @Override
    public boolean supports(Class<?> authentication) {

        return AuthToken.class.isAssignableFrom(authentication);
    }

}
