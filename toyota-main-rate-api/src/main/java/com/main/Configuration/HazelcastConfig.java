package com.main.Configuration;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration()
public class HazelcastConfig {

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
