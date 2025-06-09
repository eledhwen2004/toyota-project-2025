package com.main.database;

import com.main.entity.RateEntity;
import com.main.repository.RateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Component class for handling database operations related to exchange rates using PostgreSQL.
 * <p>
 * This class provides a method to persist a list of {@link RateEntity} objects to the database.
 */
@Component
public class PostgresqlDatabase {

    /**
     * Spring Data JPA repository used to interact with the database for {@link RateEntity} objects.
     */
    @Autowired
    private RateRepository rateRepository;

    /**
     * Saves a list of new exchange rate entities into the PostgreSQL database.
     *
     * @param rateEntityList the list of {@link RateEntity} objects to be saved
     */
    public void saveNewRates(List<RateEntity> rateEntityList) {
        rateRepository.saveAll(rateEntityList);
    }
}
