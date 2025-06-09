package com.main.Subscriber;

import com.main.Coordinator.CoordinatorInterface;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Subscriber bileşenleri için temel arayüzdür.
 * <p>
 * Bu arayüz, platformlarla bağlantı kuran, bağlantıyı sonlandıran ve belirli kurlara abone olma/olmayı bırakma
 * işlemlerini gerçekleştiren sınıflar için bir sözleşme niteliğindedir.
 * <p>
 * Uygulamalar {@link com.main.Subscriber.Subscribers.RESTSubscriber} ve {@link com.main.Subscriber.Subscribers.TCPSubscriber}
 * gibi farklı haberleşme yöntemleriyle gerçekleştirilir.
 */
@Component
public interface SubscriberInterface {

    /**
     * Verilen kullanıcı adı ve şifre ile platforma bağlanır.
     *
     * @param platformName platform adı (ör. PF1, PF2)
     * @param userName     kullanıcı adı
     * @param password     parola
     * @throws IOException bağlantı sırasında bir hata oluşursa
     */
    void connect(String platformName, String userName, String password) throws IOException;

    /**
     * Platform ile olan bağlantıyı sonlandırır.
     *
     * @param platformName platform adı
     * @param userName     kullanıcı adı
     * @param password     parola
     * @throws IOException bağlantıyı kesme sırasında bir hata oluşursa
     */
    void disConnect(String platformName, String userName, String password) throws IOException;

    /**
     * Belirtilen kura abone olur.
     *
     * @param platformName platform adı
     * @param rateName     abone olunacak kurun adı (ör. USDTRY)
     * @throws IOException abonelik işlemi sırasında bir hata oluşursa
     */
    void subscribe(String platformName, String rateName) throws IOException;

    /**
     * Daha önce abone olunan bir kurdan çıkış yapılır.
     *
     * @param platformName platform adı
     * @param rateName     abonelikten çıkılacak kurun adı
     * @throws IOException çıkış işlemi sırasında bir hata oluşursa
     */
    void unSubscribe(String platformName, String rateName) throws IOException;

    /**
     * Abone bileşenine koordinatör atanır. Koordinatör abonelik, bağlantı ve veri durumlarını kontrol eder.
     *
     * @param coordinator uygulamanın merkezindeki {@link CoordinatorInterface} örneği
     */
    void setCoordinator(CoordinatorInterface coordinator);
}
