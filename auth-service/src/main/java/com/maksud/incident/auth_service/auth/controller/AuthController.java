package com.maksud.incident.auth_service.auth.controller;

import com.maksud.incident.auth_service.auth.dto.AuthResponse;
import com.maksud.incident.auth_service.auth.dto.LoginRequest;
import com.maksud.incident.auth_service.auth.dto.LogoutRequest;
import com.maksud.incident.auth_service.auth.service.AuthService;
import com.maksud.incident.auth_service.token.dto.RefreshRequest;
import com.maksud.incident.auth_service.token.dto.RefreshResponse;
import com.maksud.incident.auth_service.user.dto.RegisterRequest;
import com.maksud.incident.auth_service.user.dto.RegisterResponse;
import com.maksud.incident.auth_service.user.entity.User;
import com.maksud.incident.auth_service.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest registerRequest){
        User user = userService.register(registerRequest);
        RegisterResponse response = new RegisterResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request, HttpServletRequest servletRequest){
        return ResponseEntity.ok(authService.login(request, servletRequest));
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshResponse> refresh(@RequestBody @Valid RefreshRequest request){
        return ResponseEntity.ok(authService.refresh(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody @Valid LogoutRequest logoutRequest){
        authService.logout(logoutRequest);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/logout-all")
    public ResponseEntity<Void> logoutAll(@RequestBody @Valid LogoutRequest logoutRequest){
        authService.logoutAll(logoutRequest);
        return ResponseEntity.noContent().build();
    }
    // test for RBAC
    @GetMapping("/user")
    @PreAuthorize("hasRole('USER')")
    public String user() {
        return "USER";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String admin() {
        return "ADMIN";
    }

    @GetMapping("/super-admin")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String superAdmin() {
        return "SUPER_ADMIN";
    }
}
