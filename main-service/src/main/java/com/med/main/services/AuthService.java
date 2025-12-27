package com.med.main.services;

import com.med.main.models.User;
import com.med.main.repo.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class AuthService {
    private final UserRepository userRepo;

    @Value("${application.jwt.secret}")
    private String secret;

    public AuthService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public User register(User user) {
        if (userRepo.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        // ВАЖНО: Добавлена валидация!
        // Если прислали JSON с полем "password" вместо "passwordHash", то user.getPasswordHash() будет null.
        if (user.getPasswordHash() == null || user.getPasswordHash().trim().isEmpty()) {
            throw new RuntimeException("Error: field 'passwordHash' is required in registration JSON");
        }
        return userRepo.save(user);
    }

    public String login(String username, String password) {
        System.out.println("Login attempt for: " + username);

        User u = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (u.getPasswordHash() == null) {
            System.err.println("Error: User has null password in DB");
            throw new RuntimeException("Server Error: User data corrupted");
        }

        if (!u.getPasswordHash().equals(password)) {
            throw new RuntimeException("Invalid password");
        }

        // --- ИСПРАВЛЕНИЕ: Проверка и Fallback для секретного ключа ---
        String jwtSecret = this.secret;
        // Если секрет null или пустой или слишком короткий
        if (jwtSecret == null || jwtSecret.trim().isEmpty() || jwtSecret.length() < 32) {
            System.err.println("CRITICAL WARNING: 'application.jwt.secret' is null/empty or too short! Using fallback secret.");
            // Используем жестко заданный ключ, если из конфига не пришел
            jwtSecret = "fallback-secret-key-that-is-very-long-and-secure-enough-for-hs256";
        }

        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

            return Jwts.builder()
                    .claim("user_id", u.getId())
                    .claim("role_id", u.getRoleId())
                    .subject(u.getUsername())
                    .issuedAt(new Date())
                    .expiration(new Date(System.currentTimeMillis() + 86400000)) // 1 день
                    .signWith(key)
                    .compact();
        } catch (Exception e) {
            e.printStackTrace(); // Важно: пишем ошибку в логи
            throw new RuntimeException("Token generation failed: " + e.getMessage());
        }
    }
}