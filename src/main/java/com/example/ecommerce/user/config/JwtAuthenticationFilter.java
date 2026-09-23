package com.example.ecommerce.user.config;

import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;

    private final JwtService jwtService;


    @Override
    protected void doFilterInternal(
            @NotNull HttpServletRequest request,
            @Nonnull  HttpServletResponse response,
            @Nonnull  FilterChain filterChain)
            throws ServletException, IOException {



        var authHeader = request.getHeader("Authorization");

        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request,response);
            return;
        }

        final var jwt = authHeader.substring(7);

        if(jwtService.isTokenValid(jwt)){

            var subject =  jwtService.extractUsername(jwt);

            var user = userDetailsService.loadUserByUsername(subject);

            var authenticatedUser = new UsernamePasswordAuthenticationToken(
                    user,
                    null,
                    user.getAuthorities()
            );

            SecurityContextHolder.getContext().setAuthentication(authenticatedUser);

        }

        filterChain.doFilter(request,response);

    }
}
