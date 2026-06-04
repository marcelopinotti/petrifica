package com.marcelo.loan.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "devices")
public class Device {
    public Device() {

    }

    public Device(UUID id, String deviceFingerprint, String userAgent, String platform, Instant createdAt) {
        this.id = id;
        this.deviceFingerprint = deviceFingerprint;
        this.userAgent = userAgent;
        this.platform = platform;
        this.createdAt = createdAt;
    }

    @Id
    private UUID id;

    @Column(name = "device_fingerprint", unique = true, nullable = false)
    private String deviceFingerprint;

    @Column(name = "user_agent")
    private String userAgent;

    private String platform;

    @Column(name = "created_at")
    private Instant createdAt;
}
