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
        // В реальном проекте тут нужен хеш пароля, но для демо оставим так
        return userRepo.save(user);
    }

    public String login(String username, String password) {
        User u = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!u.getPasswordHash().equals(password)) {
            throw new RuntimeException("Invalid password");
        }

        // ИСПРАВЛЕНО: Используем getBytes для создания ключа, это надежнее для демо
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .claim("user_id", u.getId())
                .claim("role_id", u.getRoleId())
                .subject(u.getUsername()) // Добавил subject, это хорошая практика
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 86400000)) // 1 день
                .signWith(key)
                .compact();
    }
}