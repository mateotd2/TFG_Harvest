package com.udc.fic.security.jwt;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class JwtInfo {

    /**
     * The user id.
     */
    private Long empleadoId;

    /**
     * The user name.
     */
    private String userName;

    /**
     * The role.
     */
    private List<String> roles;


}