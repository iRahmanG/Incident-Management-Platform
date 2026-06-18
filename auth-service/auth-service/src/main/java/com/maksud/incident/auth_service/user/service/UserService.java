package com.maksud.incident.auth_service.user.service;

import com.maksud.incident.auth_service.exception.EmailAlreadyExistsException;
import com.maksud.incident.auth_service.user.dto.RegisterRequest;
import com.maksud.incident.auth_service.user.entity.Role;
import com.maksud.incident.auth_service.user.entity.User;
import com.maksud.incident.auth_service.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User register(RegisterRequest request){
        if(userRepository.existsByEmail(request.getEmail())){
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        User user = User.builder()
                .id(UUID.randomUUID())
                .name(request.getName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .active(true)
                .build();

        return userRepository.save(user);
    }
}
