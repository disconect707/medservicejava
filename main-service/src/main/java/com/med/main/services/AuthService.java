package com.med.main.services;

import com.med.main.models.User;
import com.med.main.repo.RoleRepository;
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
    private final RoleRepository roleRepo;

    @Value("${application.jwt.secret}")
    private String secret;

    public AuthService(UserRepository userRepo, RoleRepository roleRepo) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
    }

    public String login(String username, String password) {
        User u = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // В реальном проекте используйте BCryptPasswordEncoder
        if (!u.getPasswordHash().equals(password)) {
            throw new RuntimeException("Invalid password");
        }

        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        return Jwts.builder()
                .claim("user_id", u.getId())
                .claim("role_id", u.getRoleId())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(key)
                .compact();
    }
}