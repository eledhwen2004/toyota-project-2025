package com.rate_api.RateList;

import com.rate_api.Entity.Rate;

import java.io.IOException;

/**
 * This interface defines the contract for managing a dynamic list of {@link Rate} objects.
 * <p>
 * Implementations should support operations such as adding, removing, and retrieving
 * rates by their names.
 */
public interface RateListInterface {

    /**
     * Adds a new {@link Rate} object to the list using the given rate name.
     * The rate's initial values (ask/bid) should be loaded from the configuration.
     *
     * @param rateName the unique name of the rate to add (e.g., "USDTRY")
     * @throws IOException if the rate configuration could not be read
     */
    void addRate(String rateName) throws IOException;

    /**
     * Removes an existing {@link Rate} object from the list and stops its simulation thread.
     *
     * @param rateName the name of the rate to remove
     */
    void removeRate(String rateName);

    /**
     * Retrieves a {@link Rate} object by its name.
     *
     * @param rateName the name of the rate to retrieve
     * @return the {@link Rate} instance, or {@code null} if not found
     */
    Rate getRate(String rateName);
}
