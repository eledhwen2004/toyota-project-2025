package com.main.Entity;

import jakarta.persistence.*;
import java.sql.Timestamp;


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
    public Timestamp rateUpdateTime;

    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", updatable = false)
    public Timestamp dbUpdateTime;

}
