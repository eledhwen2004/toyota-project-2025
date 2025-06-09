package com.main.repository;

import com.main.entity.RateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository interface for performing CRUD operations on {@link RateEntity}.
 * <p>
 * This interface provides methods like {@code save}, {@code findAll}, {@code deleteById}, etc.
 * without requiring custom implementation.
 */
@Repository
public interface RateRepository extends JpaRepository<RateEntity, Long> {
    // Additional query methods can be defined here if needed in the future
}
