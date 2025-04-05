package com.fairsharebu.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

/**
 * Utility class for JWT token generation and validation.
 */
public class JWTUtil {

    // Secret key for signing the token
    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // Token validity duration in milliseconds (7 days)
    private static final long TOKEN_VALIDITY = 7 * 24 * 60 * 60 * 1000;

    /**
     * Generate a JWT token for the given user ID and username.
     * 
     * @param userId   The user ID to include in the token
     * @param username The username to include in the token
     * @return A JWT token as a string
     */
    public static String generateToken(int userId, String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + TOKEN_VALIDITY);

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("username", username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SECRET_KEY)
                .compact();
    }

    /**
     * Validate a JWT token and extract the user ID.
     * 
     * @param token The JWT token to validate
     * @return The user ID from the token, or null if the token is invalid
     */
    public static Integer validateTokenAndGetUserId(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return Integer.parseInt(claims.getSubject());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Validate a JWT token and extract the username.
     * 
     * @param token The JWT token to validate
     * @return The username from the token, or null if the token is invalid
     */
    public static String validateTokenAndGetUsername(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return claims.get("username", String.class);
        } catch (Exception e) {
            return null;
        }
    }
}