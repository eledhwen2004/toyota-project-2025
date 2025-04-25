package com.main.Repository;

import com.main.Entity.RateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * The `RateRepository` interface provides CRUD operations for the `RateEntity` entity using Spring Data JPA.
 * This repository extends `JpaRepository` to leverage standard database operations for `RateEntity` objects.
 * <p>
 * Additionally, custom query methods are defined for specialized queries, such as finding the most recently updated rate.
 * </p>
 *
 * @see JpaRepository
 * @see RateEntity
 */
@Repository("rateRepository")
public interface RateRepository extends JpaRepository<RateEntity, Long> {

    /**
     * Finds the most recently updated rate entity based on the `dbUpdateTime` field.
     * The query is ordered by `dbUpdateTime` in descending order, returning the top result.
     *
     * @return An `Optional<RateEntity>` containing the most recently updated rate, or an empty `Optional` if no records are found.
     */
    Optional<RateEntity> findTopByOrderByDbUpdateTimeDesc();
}
