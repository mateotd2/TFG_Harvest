package com.harvest.security.jwt;

import com.harvest.security.UserDetailsImpl;

public interface JwtGenerator {
    String generateJwtToken(UserDetailsImpl userPrincipal);

    String generateTokenFromUsername(String username);

    String getUserNameFromJwtToken(String token);

    boolean validateJwtToken(String authToken);

    JwtInfo getInfo(String token);
}
