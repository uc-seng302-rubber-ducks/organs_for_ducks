package odms.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

public class AuthFilter extends AbstractAuthenticationProcessingFilter {

    public static final String TOKEN_HEADER = "x-auth-token";


    protected AuthFilter(String defaultFilterProcessesUrl) {
        super(defaultFilterProcessesUrl);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {
        final String tokenValue = getTokenValue(request);
        if(StringUtils.isEmpty(tokenValue)){
            return null;
        }

        AuthToken authToken = new AuthToken(tokenValue);
        return this.getAuthenticationManager().authenticate(authToken);
    }

    private String getTokenValue(HttpServletRequest request) {
        return Collections.list(request.getHeaderNames()).stream()
                .filter(header -> header.equalsIgnoreCase(TOKEN_HEADER))
                .map(request::getHeader).findFirst().orElse(null);
    }


}
