package odms;

import odms.commons.model._enum.Environments;
import odms.commons.utils.Log;
import odms.security.AuthProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationProvider;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableAutoConfiguration
@EnableSwagger2
public class Server {
    public static void main(String[] args) {
        Log.setup(Environments.SERVER);
        SpringApplication.run(Server.class, args);
    }

    @Bean
    public AuthenticationProvider createCustomAuthenticationProvider() {
        return new AuthProvider();
    }

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("odms.controller"))
                .paths(PathSelectors.any())
                .build();
    }
}
