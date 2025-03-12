package com.example.tracking.service;

import com.example.tracking.model.TrackingNumber;
import com.example.tracking.repository.TrackingNumberRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Service
public class TrackingNumberService {

    private final TrackingNumberRepository trackingNumberRepository;
    private final StringRedisTemplate redisTemplate;

    private static final Logger logger = LoggerFactory.getLogger(TrackingNumberService.class);

    private static final int TRACKING_NUMBER_LENGTH = 16;
    private static final String TRACKING_LOCK_PREFIX = "lock:tracking:";

    public TrackingNumberService(TrackingNumberRepository trackingNumberRepository, StringRedisTemplate redisTemplate) {
        this.trackingNumberRepository = trackingNumberRepository;
        this.redisTemplate = redisTemplate;
    }

    public String generateTrackingNumber(String origin, String destination, String weight,
                                         String createdAt, String customerId, String customerName, String customerSlug) {
        String trackingNumber = createUniqueTrackingNumber(origin, destination, customerId, weight, createdAt, customerName, customerSlug);
        String lockKey = TRACKING_LOCK_PREFIX + trackingNumber;
        ValueOperations<String, String> valueOps = redisTemplate.opsForValue();

        Boolean lockAcquired = valueOps.setIfAbsent(lockKey, "locked", 10, TimeUnit.SECONDS);
        if (lockAcquired == null || !lockAcquired) {
            logger.warn("Tracking number generation in progress for: {}", trackingNumber);
            throw new RuntimeException("Tracking number generation is in progress, please try again.");
        }

        try {
            if (redisTemplate.hasKey(trackingNumber) || trackingNumberRepository.existsByTrackingNumber(trackingNumber)) {
                logger.info("Tracking number already exists: {}", trackingNumber);
                return trackingNumber;
            }

            TrackingNumber entity = new TrackingNumber(trackingNumber, Instant.now());
            trackingNumberRepository.save(entity);
            redisTemplate.opsForValue().set(trackingNumber, "1");

        } catch (Exception e) {
            logger.error("Error while saving tracking number", e);
            throw new RuntimeException("Error while saving tracking number", e);
        } finally {
            redisTemplate.delete(lockKey);
        }

        return trackingNumber;
    }


    public String createUniqueTrackingNumber(String origin, String destination, String customerId,
                                             String weight, String createdAt, String customerName, String customerSlug) {

        String uniqueString = origin + destination + customerId + weight + createdAt + customerName + customerSlug;
        return generateDeterministicHash(uniqueString);
    }


    private String generateDeterministicHash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));

            String base36Hash = new java.math.BigInteger(1, hashBytes).toString(36).toUpperCase();

            return base36Hash.substring(0, TRACKING_NUMBER_LENGTH);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating hash for tracking number", e);
        }
    }
}
