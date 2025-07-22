package org.example.aop.jwt;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.example.security.JwtFilter;
import org.example.security.JwtUtil;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;

@Aspect
@Component
@RequiredArgsConstructor//bu lomboktan gelen bir özellik, data olarak yapmıyoruz çünkü getter-setter istemiyoruz, bu yüzden sadece final value'lere constructor oluşturması için bunu kullanıyoruz
public class TokenExpirationtimeAspect {

    private final JwtUtil jwtUtil;
    //buradaki @Before ifadesi bu anatasyonu taşıyan methodların çağrılmadan önce bunun çalışmasını sağlar
    //ayrıca buradaki ..,request ifadesi yüzünden hata almamak için bunu koyduğumuz methodun son parametresini request türünde yan HTTPServletRequest olması gerekir
    // arg(..,request) ifadesini kaldıralım ve kendimiz headeri bulalım yoksa sorun çıkıyor
    //@Before("@annotation(org.example.aop.jwt.CheckTokenExpirationTime) && args(..,request)")
    @Before("@annotation(org.example.aop.jwt.CheckTokenExpirationTime)")
    public void checkTokenExpiration(JoinPoint joinPoint) {//burada joinPoint denilen yer işte hangi methodun bağlandığını tutar, bunun üzerinden method bilgilerini alırız
        //önce boş bir request tanımlıyoruz sonra joinpoint içerindeki parametrelerden requesti alacağaız
        HttpServletRequest request = null;

        //şimdi for ile joimpoint arg'larını dolaşıp HttpServletrequest sınıfına ait herhangi bir parametre arayacağız
        for(Object arg: joinPoint.getArgs()){
            if(arg instanceof HttpServletRequest){
                request = (HttpServletRequest) arg;// burada type casting yapıyoruz, çünkü arg nesnesi object türünde ve biz bunu httpServletRequest türüne dönüştürmeliyiz
            }
        }
        if(request == null){
            throw new RuntimeException("Request is null");
        }
        String token = jwtUtil.extractTokenFromHeader(request);

        if(token == null || token.isBlank()){
            //System.out.println("Token is missing or empty in method: " + joinPoint.getSignature().getName());
            throw new RuntimeException("Token is missing or empty");
        }
        if(jwtUtil.isTokenExpired(token)){
            throw new RuntimeException("Token has been expired");
        }

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();//burada biz imzasını alıyoruz, imza dediğimizde aslında methodun ismi, parametreleri ve return type'ı gibi bilgileri alıyoruz
        String methodName = signature.getMethod().getName();// burada işte o imza üzerinden methodun ismini alıyoruz
        Object[] args = joinPoint.getArgs();//burada da imza üzerindne methodun parametrelerini alıyoruz

        System.out.println("Method: " + methodName + " called with arguments: " + Arrays.toString(args));
    }
}
