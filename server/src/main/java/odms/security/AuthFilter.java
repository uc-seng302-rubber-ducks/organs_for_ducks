package odms.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

/**
 * This class reads incoming requests for the auth token header.
 * It will grant authentication if it exists/is correct, otherwise pass the request on to the next filter (if any)
 */
public class AuthFilter extends AbstractAuthenticationProcessingFilter {

    public static final String TOKEN_HEADER = "x-auth-token";


    protected AuthFilter(RequestMatcher matcher) {
        super(matcher);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        final String token = getTokenValue((HttpServletRequest) request);

        //if no auth header found, pass it on and do nothing
        if (StringUtils.isEmpty(token)) {
            chain.doFilter(request, response);
            return;
        }

        this.setAuthenticationSuccessHandler((request1, response1, authentication) -> {
            chain.doFilter(request1, response1);
        });

        super.doFilter(request, response, chain);
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
