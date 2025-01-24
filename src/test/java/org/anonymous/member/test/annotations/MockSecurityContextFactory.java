package org.anonymous.member.test.annotations;

import org.anonymous.member.Member;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.Arrays;
import java.util.List;

public class MockSecurityContextFactory implements WithSecurityContextFactory<MockMember> {
    @Override
    public SecurityContext createSecurityContext(MockMember annotation) {
        Member member = new Member();
        member.setEmail(annotation.email());
        member.setSeq(annotation.seq());
        member.setName(annotation.name());
        member.set_authorities(Arrays.stream(annotation.authority()).toList());
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(member, null, member.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication); // 로그인 처리

        return context;
    }
}
