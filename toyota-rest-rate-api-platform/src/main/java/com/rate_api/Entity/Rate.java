package com.rate_api.Entity;

import com.rate_api.Configuration.Configuration;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.time.Instant;
import java.util.Random;

/**
 * Represents a currency rate that dynamically updates its bid and ask values at regular intervals.
 * <p>
 * The rate fluctuates using a random percentage, and the updates continue until explicitly stopped.
 * This class extends {@link Thread} and is intended to simulate real-time price updates.
 */
@Getter
@Setter
public class Rate extends Thread {

    /**
     * The name of the rate (e.g., "USDTRY", "EURUSD").
     */
    private String rateName;

    /**
     * The bid price of the rate.
     */
    private double bid;

    /**
     * The ask price of the rate.
     */
    private double ask;

    /**
     * The timestamp of the last update.
     */
    private Instant timestamp;

    /**
     * Flag indicating whether the rate should keep updating.
     */
    private boolean isActive = true;

    /**
     * Frequency (in seconds) at which the rate updates its values.
     */
    private double rateFrequency;

    /**
     * Constructs a new {@code Rate} object with the specified name, bid, and ask values.
     * The update frequency is retrieved from {@link Configuration#getRateFrequency()}.
     *
     * @param rateName the name of the rate
     * @param bid      the initial bid price
     * @param ask      the initial ask price
     * @throws IOException if the configuration file cannot be loaded
     */
    public Rate(String rateName, double bid, double ask) throws IOException {
        this.setRateName(rateName);
        this.setBid(bid);
        this.setAsk(ask);
        this.setRateFrequency(Configuration.getRateFrequency());
    }

    /**
     * Stops the rate updates by setting {@code isActive} to false.
     */
    public void stopRate() {
        isActive = false;
    }

    /**
     * Continuously updates the bid and ask prices by a small random percentage
     * at each interval, simulating real-time fluctuations.
     */
    @Override
    public void run() {
        Random rand = new Random();
        Double randRatePercentage;
        Boolean increaseOrDecrease;

        do {
            // Generate a small random percentage (max 1.1%)
            do {
                randRatePercentage = rand.nextDouble();
            } while (randRatePercentage > 0.011);

            increaseOrDecrease = rand.nextBoolean();

            if (increaseOrDecrease) {
                setAsk(ask + (ask * randRatePercentage));
                setBid(bid + (bid * randRatePercentage));
            } else {
                setAsk(ask - (ask * randRatePercentage));
                setBid(bid - (bid * randRatePercentage));
            }

            try {
                sleep((long) (rateFrequency * 1000));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            setTimestamp(Instant.now());
        } while (isActive);
    }
}
