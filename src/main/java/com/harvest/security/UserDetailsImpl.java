package com.harvest.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.harvest.empleado.Empleado;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

//@Data
@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class UserDetailsImpl implements UserDetails {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    private String username;

    private String email;

    @JsonIgnore
    private String password;


    private GrantedAuthority authoritiy;


    public static UserDetailsImpl build(Empleado empleado){
        GrantedAuthority authority =new SimpleGrantedAuthority( empleado.getRol().getName().toString());
        return new UserDetailsImpl(empleado.getId(), empleado.getUsername(), empleado.getEmail(), empleado.getPassword(), authority);
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
         List<GrantedAuthority> authorities = new ArrayList();
         authorities.add(this.authoritiy);
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
