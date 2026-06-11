package com.marcelo.loan.entity;

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

    public IpAddress() {

    }

    public IpAddress(UUID id, String ip, String country, String state, String city, String isp, Boolean isVpn, Boolean isProxy, Boolean isDatacenter, Point location, Instant createdAt) {
        this.id = id;
        this.ip = ip;
        this.country = country;
        this.state = state;
        this.city = city;
        this.isp = isp;
        this.isVpn = isVpn;
        this.isProxy = isProxy;
        this.isDatacenter = isDatacenter;
        this.location = location;
        this.createdAt = createdAt;
    }

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


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getIsp() {
        return isp;
    }

    public void setIsp(String isp) {
        this.isp = isp;
    }

    public Boolean getVpn() {
        return isVpn;
    }

    public void setVpn(Boolean vpn) {
        isVpn = vpn;
    }

    public Boolean getProxy() {
        return isProxy;
    }

    public void setProxy(Boolean proxy) {
        isProxy = proxy;
    }

    public Boolean getDatacenter() {
        return isDatacenter;
    }

    public void setDatacenter(Boolean datacenter) {
        isDatacenter = datacenter;
    }

    public Point getLocation() {
        return location;
    }

    public void setLocation(Point location) {
        this.location = location;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
