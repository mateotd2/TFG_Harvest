package com.udc.fic.security.jwt;

import com.udc.fic.security.UserDetailsImpl;

public interface JwtGenerator {
    String generateJwtToken(UserDetailsImpl userPrincipal);

    String generateTokenFromUsername(String username);

    String getUserNameFromJwtToken(String token);

    boolean validateJwtToken(String authToken);

    JwtInfo getInfo(String token);
}
