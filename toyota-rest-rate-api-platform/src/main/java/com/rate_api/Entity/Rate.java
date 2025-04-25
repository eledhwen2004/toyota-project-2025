package com.rate_api.Entity;

import com.rate_api.Configuration.Configuration;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.time.Instant;
import java.util.Random;

/**
 * Represents a rate entity that updates bid and ask prices based on a frequency.
 * This class extends {@link Thread} to run rate updates in a separate thread.
 */
@Getter
@Setter
public class Rate extends Thread {

    /**
     * The name of the rate (e.g., currency pair or asset).
     */
    private String rateName;

    /**
     * The bid price for the rate.
     */
    private double bid;

    /**
     * The ask price for the rate.
     */
    private double ask;

    /**
     * The timestamp indicating when the rate was last updated.
     */
    private Instant timestamp;

    /**
     * A flag indicating whether the rate update is active or not.
     */
    private boolean isActive = true;

    /**
     * The frequency at which the rate updates (in seconds).
     */
    private double rateFrequency;

    /**
     * Constructs a new {@link Rate} with the specified rate name, bid, and ask values.
     * The rate frequency is fetched from the configuration file.
     *
     * @param rateName the name of the rate (e.g., currency pair or asset).
     * @param bid the initial bid price for the rate.
     * @param ask the initial ask price for the rate.
     * @throws IOException if an error occurs while fetching the rate frequency from the configuration.
     */
    public Rate(String rateName, double bid, double ask) throws IOException {
        this.setRateName(rateName);
        this.setBid(bid);
        this.setAsk(ask);
        this.setRateFrequency(Configuration.getRateFrequency());
    }

    /**
     * Stops the rate updates by setting the isActive flag to false.
     */
    public void stopRate() {
        isActive = false;
    }

    /**
     * Runs the rate update process in a separate thread.
     * The bid and ask prices are updated randomly within a small percentage range at regular intervals.
     */
    @Override
    public void run() {
        Random rand = new Random();
        Double randRatePercentage;
        Boolean increaseOrDecrease;
        do {
            // Generate a random rate percentage to adjust the bid/ask prices
            do {
                randRatePercentage = rand.nextDouble();
            } while (randRatePercentage > 0.011); // Limit the random percentage to 1.1%

            increaseOrDecrease = rand.nextBoolean();
            if (increaseOrDecrease) {
                // Increase the ask and bid prices by the generated percentage
                setAsk(ask + (ask * randRatePercentage));
                setBid(bid + (bid * randRatePercentage));
            } else {
                // Decrease the ask and bid prices by the generated percentage
                setAsk(ask - (ask * randRatePercentage));
                setBid(bid - (bid * randRatePercentage));
            }

            // Sleep for the configured rate frequency (in milliseconds)
            try {
                sleep((long) (rateFrequency * 1000));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            // Update the timestamp after each rate change
            setTimestamp(Instant.now());
        } while (isActive); // Continue updating until the rate is stopped
    }
}
