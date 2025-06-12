package com.fpt.capstone.tourism.helper.IHelper;


import com.fpt.capstone.tourism.model.User;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

public interface JwtHelper {
    Claims extractAllClaims(String token);
    <T> T extractClaim(String token, Function<Claims, T> resolver);
    String extractUsername(String token);
    boolean isValid(String token, UserDetails user);
    boolean isTokenExpired(String token);
    Date extractExpiration(String token);
    String generateToken(User user);
    SecretKey getSignInKey();
    boolean isTokenValid(String token, UserDetails user);
}
