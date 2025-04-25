package com.rate_api.RateList;

import com.rate_api.Entity.Rate;

import java.io.IOException;

public interface RateListInterface {
    void addRate(String rateName) throws IOException;
    void removeRate(String rateName);
    Rate getRate(String rateName);
}
