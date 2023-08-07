package com.harvest.security;

import com.harvest.empleado.Empleado;
import com.harvest.repository.EmpleadoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    EmpleadoRepository empleadoRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Empleado> empleado = empleadoRepository.findByUsername(username);
        if (empleado.isPresent()) {
            return UserDetailsImpl.build(empleado.get());
        } else throw new UsernameNotFoundException(username);
    }
}
