package com.marcelo.loan.entity;

import jakarta.persistence.*;
import org.geolatte.geom.Point;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "customer_addresses")
public class CustomerAddress {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    private String cep;

    private String street;

    private String city;

    private String state;

    @Column(columnDefinition = "geography(Point,4326)")
    private Point location;

    @Column(name = "created_at")
    private Instant createdAt;
}
