package com.hsbc.deskflow.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "desk", uniqueConstraints = {
        @UniqueConstraint(name = "uk_desk_code", columnNames = "code")
})
public class Desk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 64)
    private String code;

    @Column(nullable = false)
    private Integer floor;

    @Column(name = "has_monitor", nullable = false)
    private boolean hasMonitor;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    public Desk() {
    }

    public Desk(String code, Integer floor, boolean hasMonitor, boolean active) {
        this.code = code;
        this.floor = floor;
        this.hasMonitor = hasMonitor;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public Integer getFloor() {
        return floor;
    }

    public boolean isHasMonitor() {
        return hasMonitor;
    }

    public boolean isActive() {
        return active;
    }
}
