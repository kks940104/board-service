package org.anonymous.member.test.annotations;

import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;
import org.anonymous.member.contants.Authority;
import org.springframework.security.test.context.support.WithSecurityContext;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
@WithSecurityContext(factory = MockSecurityContextFactory.class)
public @interface MockMember {
    long seq() default 1L;
    String email() default "user01@test.org";
    String name() default "사용자01";
    Authority[] authority() default { Authority.USER };
}