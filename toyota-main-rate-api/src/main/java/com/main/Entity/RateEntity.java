package com.main.Entity;

import jakarta.persistence.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.time.Instant;


@Entity
@Table(name = "rates")
public class RateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long id;

    @Column(nullable = false, updatable = false)
    public String rateName;

    @Column(nullable = false, updatable = false)
    public Double ask;

    @Column(nullable = false, updatable = false)
    public Double bid;

    @Column(nullable = false, updatable = false)
    public Instant rateUpdateTime;

    @UpdateTimestamp
    @Column(nullable = false)
    public Timestamp dbUpdateTime;


}
