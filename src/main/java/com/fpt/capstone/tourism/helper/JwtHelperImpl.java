package com.fpt.capstone.tourism.helper;


import com.fpt.capstone.tourism.exception.common.BusinessException;
import com.fpt.capstone.tourism.helper.IHelper.JwtHelper;
import com.fpt.capstone.tourism.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JwtHelperImpl implements JwtHelper {

    @Value("${secret-key}")
    private String SECRET_KEY;
    @Value("${expired-time}")
    private long expiredTime;

    @Override
    public Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    @Override
    public  <T> T extractClaim(String token, Function<Claims, T> resolver) {
        Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    @Override
    public  String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public  boolean isValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && isTokenExpired(token);
    }

    @Override
    public  boolean isTokenExpired(String token) {
        return extractExpiration(token).after(new Date());

    }

    @Override
    public  Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }


    @Override
    public  String generateToken(User user) {
        try {

            List<String> roles = user.getUserRoles().stream()
                    .map(userRole -> userRole.getRole().getRoleName())
                    .collect(Collectors.toList());

            return Jwts
                    .builder()
                    .subject(user.getUsername())
                    .claim("id", user.getId())
                    .claim("roles", roles)
                    .claim("createdAt", new Date())
                    .claim("expiresAt", new Date(System.currentTimeMillis() + expiredTime))
                    .issuedAt(new Date(System.currentTimeMillis()))
                    .expiration(new Date(System.currentTimeMillis() + expiredTime))
                    .signWith(getSignInKey())
                    .compact();
        } catch (Exception e) {
            throw BusinessException.of("Generated token failed", e);
        }
    }

    @Override
    public SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64URL.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public boolean isTokenValid(String token, UserDetails user) {
        String username = extractUsername(token);
        return username.equals(user.getUsername()) && isTokenExpired(token);
    }
}