package com.moonseo.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.sql.Date;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

@Component
public class JwtProvider {

    private final JwtProperties props;
    private final SecretKey accessKey;
    private final SecretKey resfreshKey;

    public JwtProvider(JwtProperties props) {
        this.props = props;
        this.accessKey = Keys.hmacShaKeyFor(props.getAccessSecret().getBytes(UTF_8));
        this.resfreshKey = Keys.hmacShaKeyFor(props.getRefreshSecret().getBytes(UTF_8));
    }

    public String createAccessToken(long userId, Set<String> roles) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(props.getAccessMinutes() * 60);

        return Jwts.builder()
                .subject(Long.toString(userId))
                .claim("tokenType", "access")
                .claim("roles", roles.stream().toList())
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(accessKey, Jwts.SIG.HS256)
                .compact();
    }

    public String createRefreshToken(long userId) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(props.getRefreshMinutes() * 60);

        return Jwts.builder()
                .subject(Long.toString(userId))
                .claim("tokenType", "refresh")
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(resfreshKey, Jwts.SIG.HS256)
                .compact();
    }

    public Jws<Claims> parseAccessToken(String token) {
        return Jwts.parser()
                .verifyWith(accessKey)
                .build()
                .parseSignedClaims(token);
    }

    public Jws<Claims> parseRefreshToken(String token) {
        return Jwts.parser()
                .verifyWith(resfreshKey)
                .build()
                .parseSignedClaims(token);
    }

    public long getUserId(Claims claims) {
        return Long.parseLong(claims.getSubject());
    }

    public Set<String> getRoles(Claims claims) {
        Object raw = claims.get("roles");
        if (raw instanceof List<?> list) {
            return list.stream()
                    .map(String::valueOf)
                    .collect(Collectors.toSet());
        }
        return Set.of();
    }
}
