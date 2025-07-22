package org.example.aop.log;

import jakarta.persistence.Id;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Aspect
@RequiredArgsConstructor
public class LogExecutionTimeAspect {

    //around methodu hem methoddan önce hem de methoddan sonra çalışır, bu sayede methodun çalışma süresini hesaplayabiliriz
    @Around("@annotation(org.example.aop.log.LogExecutionTime)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable{
        //metgodun imzasını joinpoint ile alıpyoruz ve type casting ile bunu methodsignatureye çeviriyoruz
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        //signature üzerinden methodun ismini aluyoruz
        String methodName = signature.getMethod().getName();
        //signature üzerinden methodun ait olduğu classın ismini alıyoruz
        String className = signature.getDeclaringType().getName();
        //method tam çalışmadan önceki zamanı tutuyoruz burada, bunu kullanarak methodun çalışma süresinii hesaplayacağız
        long start = System.currentTimeMillis();
        // şimdi burada biz boş bir object tanımlıyoruz bunu methodu başlatmak için kullanacağız
        Object proceed = null;
        try{
            // methodu çalıştırıyoruz
            proceed = joinPoint.proceed();
            // şimdi methoddan sonraki süreyi hesaplayacağız
            long executionTime = System.currentTimeMillis() - start;
            // loglama işlemi yapıyoruz, burada log.info ile loglama yapıyoruz, ondan evvel ama long'u string yapalım
            String executionTimeString = String.valueOf(executionTime);
            // şimdi de loglamayı yapalım
            log.info(executionTimeString + " milisaniye - " + className + "." + methodName);
            return proceed; // burada yaptığım şey şu, bu aspecti kullanan methodlar ne döndürüyolarsa bu aspectte de onu döndürüyoruz
        }
        //eğer hata gelirse de bunu yapalım
        catch(Exception e){
            throw new RuntimeException("Method execution failed: " + className + "." + methodName, e);
        }

    }
}
