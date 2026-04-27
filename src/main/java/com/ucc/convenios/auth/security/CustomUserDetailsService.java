package com.ucc.convenios.auth.security;

import com.ucc.convenios.roles.entity.UserRole;
import com.ucc.convenios.roles.repository.UserRoleRepository;
import com.ucc.convenios.users.entity.User;
import com.ucc.convenios.users.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    public CustomUserDetailsService(
            UserRepository userRepository,
            UserRoleRepository userRoleRepository
    ) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        String normalizedEmail = email.toLowerCase();

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        List<SimpleGrantedAuthority> authorities = userRoleRepository.findByUser(user)
                .stream()
                .map(UserRole::getRole)
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                .toList();

        UserBuilder builder = org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPasswordHash())
                .authorities(authorities)
                .disabled(!user.getActive());

        return builder.build();
    }
}