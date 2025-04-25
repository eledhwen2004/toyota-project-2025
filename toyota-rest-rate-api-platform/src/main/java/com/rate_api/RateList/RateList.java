package com.rate_api.RateList;

import com.rate_api.Configuration.Configuration;
import com.rate_api.Entity.Rate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages a list of {@link Rate} objects and provides methods to add, remove, and retrieve rates.
 * This class is annotated as a Spring component for dependency injection.
 */
@Component
public class RateList implements RateListInterface {

    /**
     * A list holding all the {@link Rate} objects.
     */
    private List<Rate> rateList;

    /**
     * Initializes the RateList by loading rate names from the configuration and starting corresponding {@link Rate} threads.
     *
     * @throws IOException if an error occurs while loading rate names or rate details from the configuration.
     */
    public RateList() throws IOException {
        this.rateList = new ArrayList<>();
        String []rateNames = Configuration.getRateNames();
        for (String rateName : rateNames) {
            this.addRate(rateName);
            this.getRate(rateName).start();
        }
    }

    /**
     * Adds a new rate to the list using the provided rate name.
     * The rate's ask and bid values are loaded from the configuration.
     *
     * @param rateName the name of the rate to be added.
     * @throws IOException if an error occurs while loading rate values from the configuration.
     */
    @Override
    public void addRate(String rateName) throws IOException {
        double rateAsk = Configuration.getFirstRateAsk(rateName);
        double rateBid = Configuration.getFirstRateBid(rateName);
        rateList.add(new Rate(rateName, rateAsk, rateBid));
    }

    /**
     * Removes a rate from the list based on its rate name. Stops the rate before removing it.
     *
     * @param rateName the name of the rate to be removed.
     */
    @Override
    public void removeRate(String rateName) {
        for (Rate rate : rateList) {
            if (rate.getRateName().equals(rateName)) {
                rate.stopRate();
                rateList.remove(rate);
            }
        }
    }

    /**
     * Retrieves a rate from the list based on its rate name.
     *
     * @param rateName the name of the rate to retrieve.
     * @return the {@link Rate} object if found, or null if the rate with the specified name does not exist.
     */
    @Override
    public Rate getRate(String rateName) {
        for (Rate rate : rateList) {
            if (rate.getRateName().equals(rateName)) {
                return rate;
            }
        }
        return null;
    }
}
