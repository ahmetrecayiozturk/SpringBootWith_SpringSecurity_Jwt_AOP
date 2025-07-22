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

    //burada biz anatasyon nesnesini almak için parametre ile CheckRole checkRole diyip onu kullanıyoruz, parametre olarak alındığı için baı özelliklerine erişim sağlanbilinir kolaylık olarak
    @Before("@annotation(checkRole)")
    public void validateRole(JoinPoint joinPoint, CheckRole checkRole) {
        //SecurityContextHolder üzerinden geçerli kullanıcının auth'unu alıyoruz
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        //Eğer auth null ise veya kullanıcı doğrulanmamışsa hata fırlatıyoruz
        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }
        //eğer kullanıcının auth'u doğruysa yani auth olmuşsa ve null değilse direkt auth'un içindekiler içinde herhangi bir authority yani rol varsa ya da yetkienlendirme işte
            //ve eğer bu yetkilendiirlmiş rol checkRole.value() ile eşleşiyorsa, yani bu anatasyonun içindeki role sahip ise hata fırlatmıyoruz
        boolean hasRole = auth.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + checkRole.value()));
        // Eğer kullanıcının rolü anatasyondaki role eşleşmiyorsa hata fırlatıyoruz
        if (!hasRole) {
            throw new RuntimeException("Invalid role");
        }
    }
}
