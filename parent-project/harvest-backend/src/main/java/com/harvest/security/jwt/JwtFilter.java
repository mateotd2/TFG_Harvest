package com.harvest.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JwtFilter extends BasicAuthenticationFilter {

    /**
     * The jwt generator.
     */
    private final JwtGenerator jwtGenerator;

    public JwtFilter(AuthenticationManager authenticationManager, JwtGenerator jwtGenerator) {

        super(authenticationManager);

        this.jwtGenerator = jwtGenerator;

    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeaderValue = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeaderValue == null || !authHeaderValue.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {

            String serviceToken = authHeaderValue.replace("Bearer ", "");
            JwtInfo jwtInfo = jwtGenerator.getInfo(serviceToken);

            request.setAttribute("serviceToken", serviceToken);
            request.setAttribute("empleadoId", jwtInfo.getEmpleadoId());

            configureSecurityContext(jwtInfo.getUserName(), jwtInfo.getRoles());

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(request, response);

    }

    /**
     * Configure security context.
     *
     * @param userName the user name
     * @param role     the role
     */
    private void configureSecurityContext(String userName, List<String> role) {

        Set<GrantedAuthority> authorities = new HashSet<>();

        authorities.add(new SimpleGrantedAuthority("ROLE_" + role));

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userName, null, authorities));

    }

}
