package odms.security;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * annotation for any endpoints that can only be accessed by a logged-in clinician.
 * If the endpoint should also be accessible by admins, the @IsAdmin annotation must be used as well
 *
 * @see IsAdmin
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAuthority('CLINICIAN')")
public @interface IsClinician {
}
