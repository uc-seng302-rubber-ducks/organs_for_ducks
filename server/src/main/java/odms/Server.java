package odms;

import odms.commons.model._enum.LoggerEnvironments;
import odms.commons.utils.Log;
import odms.security.AuthProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationProvider;

@SpringBootApplication
@EnableAutoConfiguration
public class Server {
    public static void main(String[] args) {
        Log.setup(LoggerEnvironments.SERVER);
        SpringApplication.run(Server.class, args);
    }

    @Bean
    public AuthenticationProvider createCustomAuthenticationProvider() {
        return new AuthProvider();
    }
}
