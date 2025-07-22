package org.example.aop.jwt;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)//bu ifade CheckTokenExpirationTime anatasyonunu yalnızca methodlar üzerinde kullanabileceğimizi ayarlar
@Retention(RetentionPolicy.RUNTIME)//bu ifade ise bu anatasyonun bilgilerinin bellekte tutulmasını ve aspect orianted programming için kullanabilmemizi sağlar
//burada interface önüne @ işareti koyuyoruz ki bu interfaceyi bir anatasyon gibi kullanabilelim
public @interface CheckTokenExpirationTime {
}
