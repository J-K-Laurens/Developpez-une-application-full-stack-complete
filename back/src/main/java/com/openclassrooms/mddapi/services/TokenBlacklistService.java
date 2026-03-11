package com.openclassrooms.mddapi.services;

import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Service for managing JWT token blacklist using Redis.
 * When a user logs out, their token is added to the blacklist
 * to prevent further use until expiration.
 * 
 * Redis is optimal for this use case:
 * - Ultra-fast lookup (in-memory)
 * - Automatic expiration with TTL
 * - No database bloat
 */
@Service
public class TokenBlacklistService {

    private static final Logger logger = LoggerFactory.getLogger(TokenBlacklistService.class);
    private static final String BLACKLIST_PREFIX = "jwt:blacklist:";

    private final RedisTemplate<String, String> redisTemplate;
    private final JwtService jwtService;

    public TokenBlacklistService(RedisTemplate<String, String> redisTemplate, JwtService jwtService) {
        this.redisTemplate = redisTemplate;
        this.jwtService = jwtService;
    }

    /**
     * Adds a JWT token to the blacklist.
     * The token is automatically removed from Redis after expiration.
     * 
     * @param token the JWT token to blacklist
     */
    public void blacklistToken(String token) {
        try {
            // Extract expiration time from token
            Claims claims = jwtService.extractAllClaims(token);
            Date expirationDate = claims.getExpiration();
            
            // Calculate remaining time until expiration
            long ttlInSeconds = (expirationDate.getTime() - System.currentTimeMillis()) / 1000;
            
            if (ttlInSeconds > 0) {
                // Store in Redis with TTL = remaining token lifetime
                String blacklistKey = BLACKLIST_PREFIX + token;
                redisTemplate.opsForValue().set(blacklistKey, "revoked", ttlInSeconds, TimeUnit.SECONDS);
                logger.info("Token blacklisted with TTL: {} seconds", ttlInSeconds);
            }
        } catch (Exception e) {
            logger.error("Error blacklisting token", e);
        }
    }

    /**
     * Checks if a token is blacklisted.
     * 
     * @param token the JWT token to check
     * @return true if the token is blacklisted, false otherwise
     */
    public boolean isTokenBlacklisted(String token) {
        try {
            String blacklistKey = BLACKLIST_PREFIX + token;
            Boolean exists = redisTemplate.hasKey(blacklistKey);
            return exists != null && exists;
        } catch (Exception e) {
            logger.error("Error checking token blacklist", e);
            // In case of Redis error, allow the request (fail-open)
            return false;
        }
    }

    /**
     * Clears the entire blacklist (useful for testing).
     * This should NOT be used in production.
     */
    public void clearBlacklist() {
        try {
            redisTemplate.getConnectionFactory()
                    .getConnection()
                    .flushAll();
            logger.warn("Entire Redis cache cleared");
        } catch (Exception e) {
            logger.error("Error clearing blacklist", e);
        }
    }
}
