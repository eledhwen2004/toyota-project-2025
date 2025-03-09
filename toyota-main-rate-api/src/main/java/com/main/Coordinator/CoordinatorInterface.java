package com.main.Coordinator;

import com.main.Dto.RateDto;
import com.main.Subscriber.RateStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public interface CoordinatorInterface {
    // Bağlantı gerçekleştiğinde çalışacak callback
    void onConnect(String platformName, Boolean status) throws IOException;

    // Bağlantı koptuğunda çalışacak callback
    void onDisConnect(String platformName, Boolean status) throws IOException;

    // istenen veri ilk defa geldiğinde
    void onRateAvailable(String platformName, String rateName, RateDto rate);

    // istenen verinin sonraki güncellemeleri
    void onRateUpdate(String platformName, String rateName, RateDto rate);

    // istenen verinin durumu ile ilgili bilgilendime
    void onRateStatus(String platformName, String rateName, RateStatus rateStatus);
}
