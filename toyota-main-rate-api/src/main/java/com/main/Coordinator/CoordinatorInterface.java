package com.main.Coordinator;

import com.main.Dto.RateDto;
import com.main.Subscriber.RateStatus;

public interface CoordinatorInterface {
    // Bağlantı gerçekleştiğinde çalışacak callback
    void onConnect(String platformName, Boolean status);

    // Bağlantı koptuğunda çalışacak callback
    void onDisConnect(String platformName, Boolean status);

    // istenen veri ilk defa geldiğinde
    void onRateAvailable(String platformName, String rateName, RateDto rate);

    // istenen verinin sonraki güncellemeleri
    void onRateUpdate(String platformName, String rateName, RateDto rate);

    // istenen verinin durumu ile ilgili bilgilendime
    void onRateStatus(String platformName, String rateName, RateStatus rateStatus);
}
