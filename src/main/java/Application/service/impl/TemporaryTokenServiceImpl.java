package Application.service.impl;

import Application.service.TemporaryTokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Map;

@Service
public class TemporaryTokenServiceImpl implements TemporaryTokenService {


    // Generate a strong random secret key (HS256 requires >= 256 bits)
    private final Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    @Override
    // Generate a temporary token valid for 15 minutes
    public Map<String, Object> generateToken(String userId) {
        String token = Jwts.builder()
                .setSubject(userId)
                .setExpiration(new Date(System.currentTimeMillis() + 15 * 60 * 1000))
                .signWith(secretKey)
                .compact();

        return Map.of("accessToken", token);
    }

    @Override
    // Validate token and return the userId (subject)
    public String validateToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return claims.getSubject();
        } catch (Exception e) {
            throw new RuntimeException("Invalid or expired temporary token");
        }
    }
}
