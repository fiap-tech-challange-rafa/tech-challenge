package br.com.fiap.techchallange.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class JwtTokenProvider {

    private final Key primaryKey;
    private final List<byte[]> externalSecrets;
    private final boolean allowUnsafeAuthLambdaTokens;
    private final ObjectMapper objectMapper;
    private final long expiration;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.auth-lambda-secret:}") String authLambdaSecret,
            @Value("${jwt.allow-unsafe-auth-lambda:false}") boolean allowUnsafeAuthLambdaTokens,
            @Value("${jwt.expiration}") long expiration
    ) {
        this.primaryKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.externalSecrets = (authLambdaSecret != null && !authLambdaSecret.isBlank())
                ? List.of(authLambdaSecret.getBytes(StandardCharsets.UTF_8))
                : List.of();
        this.allowUnsafeAuthLambdaTokens = allowUnsafeAuthLambdaTokens;
        this.objectMapper = new ObjectMapper();
        this.expiration = expiration;
    }

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(primaryKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        Claims claims = parseClaims(token);
        String subject = claims.getSubject();
        if (subject != null && !subject.isBlank()) {
            return subject;
        }

        Object username = claims.get("username");
        if (username != null && !username.toString().isBlank()) {
            return username.toString();
        }

        Object cpf = claims.get("cpf");
        if (cpf != null && !cpf.toString().isBlank()) {
            return cpf.toString();
        }

        Object clientId = claims.get("clientId");
        if (clientId != null && !clientId.toString().isBlank()) {
            return "client-" + clientId;
        }

        return "authenticated-user";
    }

    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    private Claims parseClaims(String token) {
        Optional<Claims> primaryClaims = parseClaimsWithKey(token, primaryKey);
        if (primaryClaims.isPresent()) {
            return primaryClaims.get();
        }

        for (byte[] externalSecret : externalSecrets) {
            Optional<Claims> externalClaims = parseClaimsWithBytes(token, externalSecret);
            if (externalClaims.isPresent()) {
                return externalClaims.get();
            }
        }

        if (allowUnsafeAuthLambdaTokens) {
            Optional<Claims> unverifiedClaims = parseClaimsWithoutSignature(token);
            if (unverifiedClaims.isPresent()) {
                return unverifiedClaims.get();
            }
        }

        throw new JwtException("Invalid token");
    }

    private Optional<Claims> parseClaimsWithKey(String token, Key key) {
        try {
            return Optional.of(
                    Jwts.parserBuilder()
                            .setSigningKey(key)
                            .build()
                            .parseClaimsJws(token)
                            .getBody()
            );
        } catch (JwtException e) {
            return Optional.empty();
        }
    }

    private Optional<Claims> parseClaimsWithBytes(String token, byte[] secretBytes) {
        try {
            return Optional.of(
                    Jwts.parserBuilder()
                            .setSigningKey(secretBytes)
                            .build()
                            .parseClaimsJws(token)
                            .getBody()
            );
        } catch (JwtException e) {
            return Optional.empty();
        }
    }

    private Optional<Claims> parseClaimsWithoutSignature(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length < 2) {
                return Optional.empty();
            }

            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            Map<String, Object> payload = objectMapper.readValue(payloadJson, new TypeReference<>() {});

            Object exp = payload.get("exp");
            if (exp == null) {
                return Optional.empty();
            }

            long expEpochSeconds = Long.parseLong(exp.toString());
            if (Instant.ofEpochSecond(expEpochSeconds).isBefore(Instant.now())) {
                return Optional.empty();
            }

            if (!payload.containsKey("cpf") && !payload.containsKey("clientId")) {
                return Optional.empty();
            }

            return Optional.of(Jwts.claims(payload));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
