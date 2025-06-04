package com.main.database;

import com.main.Mapper.RateMapper;
import com.main.entity.RateEntity;
import com.main.repository.RateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PostgresqlDatabase {
    @Autowired  RateRepository rateRepository;

    public void saveNewRates(List <RateEntity> rateEntityList) {
        rateRepository.saveAll(rateEntityList);
    }
}
