package com.marcelo.fraud.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.geolatte.geom.Point;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "ip_addresses")
public class IpAddress {

    @Id
    private UUID id;

    @Column(unique = true, nullable = false)
    private String ip;

    private String country;

    private String state;

    private String city;

    private String isp;

    @Column(name = "is_vpn")
    private Boolean isVpn;

    @Column(name = "is_proxy")
    private Boolean isProxy;

    @Column(name = "is_datacenter")
    private Boolean isDatacenter;

    @Column(columnDefinition = "geography(Point,4326)")
    private Point location;

    @Column(name = "created_at")
    private Instant createdAt;
}
