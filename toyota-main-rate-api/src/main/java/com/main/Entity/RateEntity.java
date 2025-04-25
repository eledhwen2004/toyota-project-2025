package com.main.Entity;

import jakarta.persistence.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.time.Instant;

/**
 * Entity class representing a rate record in the database.
 * This class maps to the "rates" table in the database and contains fields
 * like rate name, bid price, ask price, and the timestamps for rate updates
 * and database updates. It is used by JPA (Java Persistence API) to interact
 * with the database.
 */
@Entity
@Table(name = "rates")
public class RateEntity {

    /**
     * The unique identifier for this rate record in the database.
     */
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
