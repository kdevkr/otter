package kr.kdev.otter.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import kr.kdev.otter.security.exception.InvalidBearerTokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@RequiredArgsConstructor
@Component
public class JwtFactory {
    private final JwtProperties jwtProperties;
    private SecretKey secretKey;

    @PostConstruct
    public void initSecretKey() {
        String secret = jwtProperties.getSecret();
        secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public JwtToken generateToken(String id) {
        TokenInfo accessToken = generateAccessToken(id);
        TokenInfo refreshToken = generateRefreshToken(id);
        return new JwtToken().setAccessToken(accessToken.getToken()).setExpireAt(accessToken.getExpiresAt())
                .setRefreshToken(refreshToken.getToken()).setRefreshExpireAt(refreshToken.getExpiresAt());
    }

    protected TokenInfo generateAccessToken(String id) {
        Date expiration = new Date(System.currentTimeMillis() + jwtProperties.getExpires().toMillis());
        String token = Jwts.builder()
                .claim("type", "access")
                .subject(id)
                .issuer("otter")
                .issuedAt(new Date())
                .expiration(expiration)
                .signWith(secretKey, Jwts.SIG.HS512)
                .compact();
        return new TokenInfo().setToken(token).setExpiresAt(expiration.toInstant());
    }

    protected TokenInfo generateRefreshToken(String id) {
        Date expiration = new Date(System.currentTimeMillis() + jwtProperties.getRefreshExpires().toMillis());
        String token = Jwts.builder()
                .claim("type", "refresh")
                .subject(id)
                .issuer("otter")
                .issuedAt(new Date())
                .expiration(expiration)
                .signWith(secretKey, Jwts.SIG.HS512)
                .compact();
        return new TokenInfo().setToken(token).setExpiresAt(expiration.toInstant());
    }

    public Jws<Claims> parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .requireIssuer("otter")
                    .build()
                    .parseSignedClaims(token);
        } catch (JwtException e) {
            throw new InvalidBearerTokenException(e.getMessage(), e);
        }
    }

}
