package com.example.tracking.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "tracking_numbers", uniqueConstraints = @UniqueConstraint(columnNames = "trackingNumber"))
public class TrackingNumber {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 16)
    private String trackingNumber;

    @Column(nullable = false)
    private Instant createdAt;

    public TrackingNumber() {}

    public TrackingNumber(String trackingNumber, Instant createdAt) {
        this.trackingNumber = trackingNumber;
        this.createdAt = createdAt;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
