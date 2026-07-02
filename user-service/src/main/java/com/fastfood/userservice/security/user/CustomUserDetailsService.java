package com.fastfood.userservice.security.user;

import com.fastfood.userservice.entity.User;
import com.fastfood.userservice.repository.UserRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository repository;

    public CustomUserDetailsService(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = repository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found"));
        return CustomUserDetails.fromUser(user);
    }
}