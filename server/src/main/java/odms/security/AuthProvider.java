package odms.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class AuthProvider implements AuthenticationProvider {

    @Autowired
    TokenStore store;

    @Override
    public Authentication authenticate(Authentication auth) throws AuthenticationException {
        final Token tokenContainer = (Token) auth;
        final String token = tokenContainer.getToken();

        if (!store.contains(token)) {
            throw new BadCredentialsException("Invalid token: " + token);
        }

        return store.get(token);
    }

    @Override
    public boolean supports(Class<?> authentication) {

        return Token.class.isAssignableFrom(authentication);
    }

}
