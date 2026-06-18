package com.maksud.incident.auth_service.security;

import com.maksud.incident.auth_service.user.entity.User;
import com.maksud.incident.auth_service.user.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserRepository userRepository;
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }
        String jwt = authHeader.substring(7);
        try{
            if(!jwtService.isTokenValid(jwt)){
                filterChain.doFilter(request, response);
                return;
            }

            String userId = jwtService.extractUserId(jwt);
            User user = userRepository.findById(UUID.fromString(userId)).orElse(null);
            if(user == null || !user.isActive() || user.getDeletedAt() !=null){
                filterChain.doFilter(request, response);
                return;
            }
            UserPrincipal principal = new UserPrincipal(user.getId(), user.getEmail(), user.getRole());
            SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_"+user.getRole().name());
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(principal, null, List.of(authority));
            System.out.println(authenticationToken.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        } catch (Exception e){
            SecurityContextHolder.clearContext();
        }
        filterChain.doFilter(request, response);
    }
}
