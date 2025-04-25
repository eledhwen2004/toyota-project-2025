package com.main.Rate;

import com.main.Configuration.Configuration;
import com.main.Server.Server;

import java.io.IOException;
import java.time.Instant;
import java.util.Random;

/**
 * Represents a rate that fluctuates over time. This class is responsible for
 * managing the bid and ask prices, as well as updating them periodically based
 * on random fluctuations. The rate is tied to a {@link Server} and runs as
 * a separate thread to continuously update the rate.
 */
public class Rate extends Thread {

    private Server server;
    private String rateName;
    private double bid;
    private double ask;
    private Instant timestamp;
    private double rateFrequency;

    /**
     * Constructor to initialize a Rate object with the given parameters.
     *
     * @param server the server that this rate is associated with.
     * @param rateName the name of the rate (e.g., "USD/EUR").
     * @param bid the initial bid price for the rate.
     * @param ask the initial ask price for the rate.
     * @throws IOException if there is an issue retrieving configuration values.
     */
    public Rate(Server server, String rateName, double bid, double ask) throws IOException {
        this.server = server;
        this.setRateName(rateName);
        this.setBid(bid);
        this.setAsk(ask);
        this.setRateFrequency(Configuration.getRateFrequency());
    }

    /**
     * Gets the name of the rate.
     *
     * @return the rate name.
     */
    public String getRateName() {
        return rateName;
    }

    /**
     * Sets the name of the rate.
     *
     * @param rateName the name of the rate.
     */
    private void setRateName(String rateName) {
        this.rateName = rateName;
    }

    /**
     * Gets the current bid price of the rate.
     *
     * @return the current bid price.
     */
    public double getBid() {
        return bid;
    }

    /**
     * Sets the bid price for the rate.
     *
     * @param bid the new bid price.
     */
    private void setBid(double bid) {
        this.bid = bid;
    }

    /**
     * Gets the current ask price of the rate.
     *
     * @return the current ask price.
     */
    public double getAsk() {
        return ask;
    }

    /**
     * Sets the ask price for the rate.
     *
     * @param ask the new ask price.
     */
    private void setAsk(double ask) {
        this.ask = ask;
    }

    /**
     * Gets the timestamp of the last rate update.
     *
     * @return the timestamp of the last update.
     */
    public Instant getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the timestamp for the last rate update.
     *
     * @param timestamp the timestamp to set.
     */
    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Gets the frequency of the rate updates in seconds.
     *
     * @return the rate update frequency.
     */
    public double getRateFrequency() {
        return rateFrequency;
    }

    /**
     * Sets the frequency of the rate updates in seconds.
     *
     * @param rateFrequency the update frequency in seconds.
     */
    public void setRateFrequency(double rateFrequency) {
        this.rateFrequency = rateFrequency;
    }

    /**
     * The run method for the thread. It simulates rate fluctuations by
     * randomly increasing or decreasing the bid and ask prices at intervals.
     * The rate updates continue until the associated server is no longer active.
     */
    @Override
    public void run() {
        Random rand = new Random();
        Double randRatePercentage;
        Boolean increaseOrDecrease;
        do {
            // Generate a random fluctuation percentage
            do {
                randRatePercentage = rand.nextDouble();
            } while (randRatePercentage > 0.011); // limit fluctuation to 1.1%

            increaseOrDecrease = rand.nextBoolean();
            if (increaseOrDecrease) {
                ask += ask * randRatePercentage; // Increase ask and bid prices
                bid += bid * randRatePercentage;
            } else {
                ask -= ask * randRatePercentage; // Decrease ask and bid prices
                bid -= bid * randRatePercentage;
            }

            // Sleep for the specified rate frequency
            try {
                sleep((long) (rateFrequency * 1000));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            // Update the timestamp after each rate change
            timestamp = Instant.now();
        } while (server.getServerStatus()); // Continue while the server is active
    }
}
