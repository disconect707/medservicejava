package com.med.main.services;

import com.med.main.models.User;
import com.med.main.repo.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class AuthService {
    private final UserRepository userRepo;

    @Value("${application.jwt.secret}")
    private String secret;

    public AuthService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    // НОВЫЙ МЕТОД: РЕГИСТРАЦИЯ
    public User register(User user) {
        if (userRepo.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        // В продакшене здесь нужно хешировать пароль (BCrypt)
        // user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        return userRepo.save(user);
    }

    public String login(String username, String password) {
        User u = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!u.getPasswordHash().equals(password)) {
            throw new RuntimeException("Invalid password");
        }

        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        return Jwts.builder()
                .claim("user_id", u.getId())
                .claim("role_id", u.getRoleId())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 день
                .signWith(key)
                .compact();
    }
}