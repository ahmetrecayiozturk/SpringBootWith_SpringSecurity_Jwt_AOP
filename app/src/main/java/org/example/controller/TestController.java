package org.example.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.example.aop.exception.Exception;
import org.example.aop.jwt.CheckTokenExpirationTime;
import org.example.aop.log.LogExecutionTime;
import org.example.aop.role.CheckRole;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/test")
public class TestController {

    @LogExecutionTime
    @CheckTokenExpirationTime
    @GetMapping("/jwt-test")
    //burada httpservletrequesti ekledik çünkü bunu biz aspectte arglar içinde kontrol edip kullanacağız
    public ResponseEntity<String> test(HttpServletRequest request) {
        return ResponseEntity.ok(Map.of("message", "kullanıcı doğrulandı").toString());
    }

    @LogExecutionTime
    //bizim oluşturduğumuz role kontrolü için bir anatasyon yazdık, bu anatasyonu kullanarak admin rolüne sahip kullanıcıların erişimini sağlıyoruz
    @CheckRole("USER")
    @GetMapping("/user-role-test")
    //burada httpservletrequeste gerek yok checkrole için ama ben kalsın istiyorum başka şeylere erişebilirim çünkü bununla
    public ResponseEntity<String> userRoleTest(HttpServletRequest request){
        return ResponseEntity.ok("user role test endpoint is working");
    }

    @LogExecutionTime
    //bizim oluşturduğumuz role annotationi ile kontrol edeceğiz
    @CheckRole("ADMIN")
    @GetMapping("/admin-role-test")
    //burada httpservletrequeste gerek yok checkrole için ama ben kalsın istiyorum başka şeylere erişebilirim çünkü bununla
    public ResponseEntity<String> adminRoleTest(HttpServletRequest request){
        return ResponseEntity.ok("admin role test endpoint is working");
    }

    @LogExecutionTime
    //bizim oluşturduğumuz exception annotation'ı ile test edeceğiz
    @Exception
    @GetMapping("/exception-test")
    public ResponseEntity<String> exceptionTest(HttpServletRequest request) {
        throw new RuntimeException("this exception has been thrown from exception annotation");
    }
}
