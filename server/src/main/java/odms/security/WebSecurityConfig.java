package odms.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.util.matcher.AndRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * This is where all security for the server is set up. all endpoints are protected, unless stated.
     *
     * @param http HttpSecurity object to add custom filters to
     * @throws Exception generic exception thrown during setup
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilterBefore(createCustomFilter(), AnonymousAuthenticationFilter.class).csrf().disable();
    }

    protected AbstractAuthenticationProcessingFilter createCustomFilter() throws Exception {
        AuthFilter filter = new AuthFilter(new NegatedRequestMatcher(
                new AndRequestMatcher(
                        //list any unprotected endpoints here
                        //it can end with /** as a wildcard
                        new AntPathRequestMatcher("/login")
                        //, new AntPathRequestMatcher("/myEndpoint")
                )
        ));
        filter.setAuthenticationManager(authenticationManagerBean());
        return filter;
    }
}
