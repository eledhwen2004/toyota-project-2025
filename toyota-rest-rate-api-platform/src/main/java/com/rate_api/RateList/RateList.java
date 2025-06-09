package com.rate_api.RateList;

import com.rate_api.Configuration.Configuration;
import com.rate_api.Entity.Rate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a list of active exchange rates, each running as a thread.
 * <p>
 * Rates are initialized based on names defined in the {@code config.properties} file.
 * Each rate simulates bid/ask updates over time.
 *
 * @see com.rate_api.Entity.Rate
 * @see com.rate_api.Configuration.Configuration
 */
@Component
public class RateList implements RateListInterface {

    private List<Rate> rateList;

    /**
     * Constructs a new {@code RateList} instance and initializes it with rates
     * defined in the configuration. Each rate is started immediately.
     *
     * @throws IOException if configuration properties fail to load
     */
    public RateList() throws IOException {
        this.rateList = new ArrayList<>();
        String[] rateNames = Configuration.getRateNames();
        for (String rateName : rateNames) {
            this.addRate(rateName);
            this.getRate(rateName).start();
        }
    }

    /**
     * Adds a new {@link Rate} to the list using the configured initial bid/ask.
     *
     * @param rateName the name of the rate to add
     * @throws IOException if configuration fails to provide rate values
     */
    @Override
    public void addRate(String rateName) throws IOException {
        double rateAsk = Configuration.getFirstRateAsk(rateName);
        double rateBid = Configuration.getFirstRateBid(rateName);
        rateList.add(new Rate(rateName, rateAsk, rateBid));
    }

    /**
     * Stops and removes a {@link Rate} by name.
     *
     * @param rateName the name of the rate to remove
     */
    @Override
    public void removeRate(String rateName) {
        for (Rate rate : rateList) {
            if (rate.getRateName().equals(rateName)) {
                rate.stopRate();
                rateList.remove(rate);
                break;
            }
        }
    }

    /**
     * Retrieves a {@link Rate} instance by name.
     *
     * @param rateName the name of the rate to retrieve
     * @return the {@code Rate} instance, or {@code null} if not found
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
