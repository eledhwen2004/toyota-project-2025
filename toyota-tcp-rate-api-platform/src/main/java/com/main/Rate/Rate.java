package com.main.Rate;

import com.main.Configuration.Configuration;
import com.main.Server.Server;

import java.io.IOException;
import java.time.Instant;
import java.util.Random;

/**
 * Represents a single exchange rate with dynamic bid/ask updates running in a separate thread.
 * <p>
 * This class simulates real-time fluctuations in bid and ask prices using random percentages.
 * The changes are driven by a configurable frequency and continue as long as the server is active.
 */
public class Rate extends Thread {

    private Server server;
    private String rateName;
    private double bid;
    private double ask;
    private Instant timestamp;
    private double rateFrequency;

    /**
     * Constructs a new {@code Rate} instance with initial values and configuration.
     *
     * @param server   the associated {@link Server} instance controlling the rate's activity
     * @param rateName the unique name of the rate (e.g., "USDTRY")
     * @param bid      the initial bid value
     * @param ask      the initial ask value
     * @throws IOException if the frequency configuration cannot be read
     */
    public Rate(Server server, String rateName, double bid, double ask) throws IOException {
        this.server = server;
        this.setRateName(rateName);
        this.setBid(bid);
        this.setAsk(ask);
        this.setRateFrequency(Configuration.getRateFrequency());
    }

    /**
     * Returns the name of the rate.
     *
     * @return the rate name
     */
    public String getRateName() {
        return rateName;
    }

    private void setRateName(String rateName) {
        this.rateName = rateName;
    }

    /**
     * Returns the current bid value.
     *
     * @return the bid value
     */
    public double getBid() {
        return bid;
    }

    private void setBid(double bid) {
        this.bid = bid;
    }

    /**
     * Returns the current ask value.
     *
     * @return the ask value
     */
    public double getAsk() {
        return ask;
    }

    private void setAsk(double ask) {
        this.ask = ask;
    }

    /**
     * Returns the last update timestamp.
     *
     * @return the timestamp as {@link Instant}
     */
    public Instant getTimestamp() {
        return timestamp;
    }

    /**
     * Updates the timestamp with a new value.
     *
     * @param timestamp the new timestamp
     */
    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Returns the rate update frequency (in seconds).
     *
     * @return the update frequency
     */
    public double getRateFrequency() {
        return rateFrequency;
    }

    public void setRateFrequency(double rateFrequency) {
        this.rateFrequency = rateFrequency;
    }

    /**
     * Periodically updates the rate values with small random changes
     * and sets a new timestamp for each change. This loop continues
     * as long as the associated server is active.
     */
    @Override
    public void run() {
        Random rand = new Random();
        Double randRatePercentage;
        Boolean increaseOrDecrease;
        do {
            // Only allow small fluctuations up to 1.1%
            do {
                randRatePercentage = rand.nextDouble();
            } while (randRatePercentage > 0.011);

            increaseOrDecrease = rand.nextBoolean();
            if (increaseOrDecrease) {
                ask += ask * randRatePercentage;
                bid += bid * randRatePercentage;
            } else {
                ask -= ask * randRatePercentage;
                bid -= bid * randRatePercentage;
            }

            try {
                sleep((long) (rateFrequency * 1000));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            timestamp = Instant.now();

        } while (server.getServerStatus());
    }
}
