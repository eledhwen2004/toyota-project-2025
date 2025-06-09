package com.main.Configuration;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration class for customizing the Hazelcast instance used in the application.
 * <p>
 * Defines cache settings for raw and calculated rate maps with a TTL (time-to-live) of 300 seconds.
 */
@Configuration
public class HazelcastConfig {

    /**
     * Creates and configures a Hazelcast {@link Config} bean named {@code customHazelcastConfig}.
     * <p>
     * This configuration:
     * <ul>
     *   <li>Sets the Hazelcast instance name to {@code hazelcast-instance}</li>
     *   <li>Defines a map named {@code raw-rates} with a TTL of 300 seconds</li>
     *   <li>Defines a map named {@code calculated-rates} with a TTL of 300 seconds</li>
     * </ul>
     *
     * @return a customized Hazelcast {@link Config} object
     */
    @Bean(name = "customHazelcastConfig")
    public Config CustomHazelcastConfig() {
        Config config = new Config();
        config.setInstanceName("hazelcast-instance");

        MapConfig rawRateMapConfig = new MapConfig();
        rawRateMapConfig.setName("raw-rates").setTimeToLiveSeconds(300);

        MapConfig calculatedRateMapConfig = new MapConfig();
        calculatedRateMapConfig.setName("calculated-rates").setTimeToLiveSeconds(300);

        config.addMapConfig(rawRateMapConfig);
        config.addMapConfig(calculatedRateMapConfig);

        return config;
    }
}
