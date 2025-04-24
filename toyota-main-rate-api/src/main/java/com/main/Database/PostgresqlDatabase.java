package com.main.Database;

import com.main.Dto.RateDto;
import com.main.Entity.RateEntity;
import com.main.Kafka.RateEvent.RateEventConsumer;
import com.main.Mapper.RateMapper;
import com.main.Repository.RateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component("postgresqlDatabase")
public class PostgresqlDatabase{

    @Autowired RateRepository rateRepository;
    private final Logger logger = LoggerFactory.getLogger("DatabaseLogger");

    PostgresqlDatabase(){
    }

    public void updateRateTable(List<RateDto> rateDtoList){
        logger.info("Rate Event Consumed: " + rateDtoList.size());
        List<RateEntity> rateEntityList = new ArrayList<>();
        for(RateDto rateDto : rateDtoList){
            rateEntityList.add(RateMapper.rateDtoToRateEntity(rateDto));
        }
        rateRepository.saveAll(rateEntityList);
        logger.info("Rate table updated for Postgresql Database");
    }

    public RateDto getLatestRateByName(String rateName){
        logger.info("Fetching latest added rate {} from Postgresql DB" , rateName);
        Optional<RateEntity> rateEntity = rateRepository.findTopByOrderByDbUpdateTimeDesc();
        if(rateEntity.isEmpty()){
            logger.info("There is no Rate found for name {} on Postgresql DB", rateName);
            return null;
        }
        logger.info("Rate {} fetched  from Postgresql DB" , rateName);
        return RateMapper.rateEntityToRateDto(rateEntity.get());
    }

}
