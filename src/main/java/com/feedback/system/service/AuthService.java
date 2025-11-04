package com.feedback.system.service;

import com.feedback.system.dto.AuthResponse;
import com.feedback.system.dto.LoginRequest;
import com.feedback.system.model.User;
import com.feedback.system.repository.UserRepository;
import com.feedback.system.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Serviço de autenticação.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Realiza login e retorna token JWT.
     */
    public AuthResponse login(LoginRequest request) {
        log.info("Tentativa de login para: {}", request.getEmail());

        // Autentica o usuário
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Carrega os detalhes do usuário
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());

        // Gera o token JWT
        String token = jwtUtil.generateToken(userDetails);

        // Busca informações adicionais do usuário
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        log.info("Login realizado com sucesso para: {}", request.getEmail());

        return new AuthResponse(token, user.getEmail(), user.getName(), user.getRole().name());
    }

    /**
     * Registra um novo usuário (para testes).
     */
    public User registerUser(String email, String password, String name, User.Role role) {
        log.info("Registrando novo usuário: {}", email);

        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email já cadastrado");
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setName(name);
        user.setRole(role);
        user.setActive(true);

        User savedUser = userRepository.save(user);
        log.info("Usuário registrado com sucesso: {}", email);

        return savedUser;
    }
}
