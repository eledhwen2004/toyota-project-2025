package com.main.Configuration;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The HazelcastConfig class configures Hazelcast settings for the application,
 * specifically setting up custom configurations for Hazelcast maps used for
 * caching purposes. This configuration is used by the Spring context to
 * instantiate the Hazelcast instance with custom settings.
 * <p>
 * This class provides configuration for two maps: "raw-rates" and "calculated-rates",
 * both of which have a Time-to-Live (TTL) set to 300 seconds.
 * </p>
 */
@Configuration
public class HazelcastConfig {

    /**
     * Configures and returns a custom Hazelcast configuration with specific
     * settings for the "raw-rates" and "calculated-rates" maps.
     * <p>
     * The configuration sets up a Hazelcast instance with the name "hazelcast-instance",
     * and applies a Time-to-Live (TTL) of 300 seconds to both maps.
     * </p>
     *
     * @return A Hazelcast configuration object with custom map settings.
     */
    @Bean(name = "customHazelcastConfig")
    public Config CustomHazelcastConfig() {
        // Creating the Hazelcast configuration
        Config config = new Config();
        config.setInstanceName("hazelcast-instance");

        // Configuring the "raw-rates" map
        MapConfig rawRateMapConfig = new MapConfig();
        rawRateMapConfig.setName("raw-rates").setTimeToLiveSeconds(300);

        // Configuring the "calculated-rates" map
        MapConfig calculatedRateMapConfig = new MapConfig();
        calculatedRateMapConfig.setName("calculated-rates").setTimeToLiveSeconds(300);

        // Adding the map configurations to the Hazelcast config
        config.addMapConfig(rawRateMapConfig);
        config.addMapConfig(calculatedRateMapConfig);

        return config;
    }

}
