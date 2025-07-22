package org.example.aop.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
@Slf4j//logger ile yazmak için
@Aspect
@Component
@RequiredArgsConstructor
public class ExceptionAspect {


    @AfterThrowing(pointcut = "@annotation(org.example.aop.exception.Exception)", throwing = "ex")
    public void logException(JoinPoint joinPoint, Throwable ex) {
        //methoda ait parametreleri bir object listesinde tutuyoruz
        Object[] args = joinPoint.getArgs();
        //metgodun imzasını alıyoruz, bu sayede methoda ait bilgileri alabiliyoruz
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        //methodun ismini ve class ismini imza üzerinden alıyoruz
        String methodName = signature.getMethod().getName();
        //methodun ait olduğu classın ismini signature üzerinden alıyoruz
        String className = signature.getMethod().getDeclaringClass().getSimpleName();
        //bir stringbuilder ile arg'ları buraya atacağız
        StringBuilder argsString = new StringBuilder();
        //şimdi joinpointten aldığımız parametreleri for ile dönüp bunu string listesine atalım
        for (Object arg : args) {
            if (arg != null) {
                argsString.append(arg.toString()).append(", ");
            } else {
                argsString.append("null, ");
            }
        }
        //liste sonundaki boşluk ve virgülü kaldırıyoruz bu şeyden dolayı argstring.append(arg.toString()).append(", "); yaotığımız yerde her eklenme sonrası virgül ve boşluk olduğundan
        //dolayı onları en sonda bi kere kaldırıyoruz
        if (argsString.length() > 0) {
            argsString.setLength(argsString.length() - 2);
        }

        log.error(className + "." + methodName + "(" + argsString + ")", ex);
        System.out.println("Exception in method: " + className + "." + methodName +
                           " with arguments: [" + argsString.toString() +
                           "] - Exception message: " + ex.getMessage());
    }
}
