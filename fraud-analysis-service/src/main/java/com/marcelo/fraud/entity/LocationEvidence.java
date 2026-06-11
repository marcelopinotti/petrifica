package com.marcelo.fraud.entity;

import jakarta.persistence.*;
import org.geolatte.geom.Point;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "location_evidences")
public class LocationEvidence {
    @Id
    private UUID id;

    @JoinColumn(name = "loan_id", nullable = false)
    private UUID loanId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ip_address_id")
    private IpAddress ipAddress;

    @Column(name = "gps_location", columnDefinition = "geography(Point,4326)")
    private Point gpsLocation;

    @Column(name = "gps_accuracy_meters")
    private BigDecimal gpsAccuracyMeters;

    @Column(name = "distance_ip_to_address_meters")
    private BigDecimal distanceIpToAddressMeters;

    @Column(name = "distance_gps_to_address_meters")
    private BigDecimal distanceGpsToAddressMeters;

    @Column(name = "created_at")
    private Instant createdAt;
}
