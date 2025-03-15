package com.main.Database;

import com.main.Dto.RateDto;
import com.main.Entity.RateEntity;
import com.main.Kafka.RateEvent.RateEventConsumer;
import com.main.Kafka.RateEvent.RateEventProducer;
import com.main.Mapper.RateMapper;
import com.main.Repository.RateRepository;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component("postgresqlDatabase")
public class PostgresqlDatabase{

    @Autowired RateRepository rateRepository;
    private final RateEventConsumer rateEventConsumer;
    private final Logger logger = LoggerFactory.getLogger("DatabaseLogger");

    PostgresqlDatabase(){
        rateEventConsumer = new RateEventConsumer();
    }

    public void updateDatabase(){
        List<RateDto> rateDtoList = rateEventConsumer.consumeRateEvent();
        logger.info("Rate Event Consumed: " + rateDtoList.size());
        List<RateEntity> rateEntityList = new ArrayList<>();
        for(RateDto rateDto : rateDtoList){
            rateEntityList.add(RateMapper.rateDtoToRateEntity(rateDto));
        }
        for(RateEntity rateEntity : rateEntityList){
            rateRepository.save(rateEntity);
        }
        logger.info("Database updated");
    }

}
