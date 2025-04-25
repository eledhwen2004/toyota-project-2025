package com.main.Database;

import com.main.Dto.RateDto;
import com.main.Entity.RateEntity;
import com.main.Kafka.RateEvent.RateEventConsumer;
import com.main.Mapper.RateMapper;
import com.main.Repository.RateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The PostgresqlDatabase class interacts with the PostgreSQL database for
 * performing operations related to rate data. It handles updating the rate
 * table and retrieving the latest rate by name.
 * <p>
 * This class is annotated as a Spring Component, making it eligible for
 * dependency injection and management by the Spring container.
 * </p>
 */
@Component("postgresqlDatabase")
public class PostgresqlDatabase {

    @Autowired
    private RateRepository rateRepository;

    private final Logger logger = LoggerFactory.getLogger("DatabaseLogger");

    /**
     * Default constructor.
     */
    PostgresqlDatabase() {
    }

    /**
     * Updates the rate table with the provided list of RateDto objects. Each RateDto
     * is mapped to a RateEntity and saved into the PostgreSQL database.
     *
     * @param rateDtoList the list of RateDto objects to be updated in the rate table.
     */
    public void updateRateTable(List<RateDto> rateDtoList) {
        logger.info("Rate Event Consumed: " + rateDtoList.size());
        List<RateEntity> rateEntityList = new ArrayList<>();
        for (RateDto rateDto : rateDtoList) {
            rateEntityList.add(RateMapper.rateDtoToRateEntity(rateDto));
        }
        rateRepository.saveAll(rateEntityList);
        logger.info("Rate table updated for Postgresql Database");
    }

    /**
     * Fetches the latest added rate by its name from the PostgreSQL database.
     * <p>
     * This method retrieves the most recently updated rate, ordered by the
     * database's update timestamp.
     * </p>
     *
     * @param rateName the name of the rate to fetch.
     * @return the latest RateDto associated with the given rate name, or null
     *         if no matching rate is found.
     */
    public RateDto getLatestRateByName(String rateName) {
        logger.info("Fetching latest added rate {} from Postgresql DB", rateName);
        Optional<RateEntity> rateEntity = rateRepository.findTopByOrderByDbUpdateTimeDesc();
        if (rateEntity.isEmpty()) {
            logger.warn("There is no Rate found for name {} on Postgresql DB", rateName);
            return null;
        }
        logger.info("Rate {} fetched from Postgresql DB", rateName);
        return RateMapper.rateEntityToRateDto(rateEntity.get());
    }
}
