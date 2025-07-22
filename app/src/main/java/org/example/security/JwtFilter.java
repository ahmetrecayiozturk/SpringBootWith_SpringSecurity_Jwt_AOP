package org.example.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        //Login ve Register endpointlerini filtreleme, yani bu endpointlere gelen isteklerde token kontrolü yapmıyoruz
        String path = request.getServletPath();
        if (path.equals("/auth/login") || path.equals("/auth/register")) {
            filterChain.doFilter(request, response);
            return;
        }
        //önce null olarak token değişkenini atıyoruz
        String token = null;
        //sonra da request ile headerdan tokeni alıyoruz
        token = jwtUtil.extractTokenFromHeader(request);
        //bu tokeni kullanarak usernameyi buluyoruz
        String username = jwtUtil.extractUsername(token);
        //3. eğer username boş değilse ve authenticate olmamış ise user
        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){
            //3.1 bir user details oluşturuyoruz ki bunu tokeni validate ederken kullanabilelim
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            //3.2 tokeni username ile valide ediyoruz ve eğer doğru ise bu bu tokeni securitycontextholder'daki token yapıyoruz
            if(jwtUtil.validateToken(token, userDetails)){
                UsernamePasswordAuthenticationToken authToken  = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        //4. her bir requestte bu zincir devam etsin istiyoruz
        filterChain.doFilter(request, response);
    }

}
