package org.example.aop.role;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.example.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

;

@Component
@Aspect
@RequiredArgsConstructor
public class CheckRoleAspect {

    @Before("@annotation(checkRole)")
    public void validateRole(JoinPoint joinPoint, CheckRole checkRole) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }
        boolean hasRole = auth.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + checkRole.value()));
        if (!hasRole) {
            throw new RuntimeException("Invalid role");
        }
    }
}
