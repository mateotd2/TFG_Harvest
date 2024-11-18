package com.udc.fic.security.jwt;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
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

    public JwtInfo(Long empleadoId, String userName, List<String> roles) {
        this.empleadoId = empleadoId;
        this.userName = userName;
        if (roles != null) {
            this.roles = new ArrayList<>(roles);
        } else {
            this.roles = null;
        }
    }

    public void setRoles(List<String> roles) {
        this.roles = new ArrayList<>(roles);
    }
}