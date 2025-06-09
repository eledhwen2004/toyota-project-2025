# 📈 Forex Rate Distribution System

Bu proje, TCP ve REST tabanlı döviz kuru sağlayıcılardan veri toplayan, bu verileri işleyip hesaplayan, Kafka aracılığıyla dağıtan ve PostgreSQL ile OpenSearch gibi sistemlere aktaran modüler, Docker tabanlı bir sistemdir.

---

##  İçindekiler

- [Proje Özeti](#proje-özeti)
- [Sistem Gereksinimleri](#sistem-gereksinimleri)
- [Kurulum Adımları](#kurulum-adımları)
- [Mimari Bileşenler](#mimari-bileşenler)
- [Kafka Yapılandırması](#kafka-yapılandırması)
- [Loglama ve Gözlemlenebilirlik](#loglama-ve-gözlemlenebilirlik)

---

##  Proje Özeti

Bu sistem, farklı veri kaynaklarından gelen döviz kuru verilerini gerçek zamanlı olarak toplayarak hesaplamakta, loglamakta ve bu verileri PostgreSQL ile OpenSearch’e göndermektedir. Platformlar arası veri tutarlılığı için dinamik hesaplama metodolojileri ve tolerans kontrolleri bulunmaktadır.

---

##  Sistem Gereksinimleri

### 1. Docker
- [Docker Desktop](https://www.docker.com/products/docker-desktop)
- Test:
  ```bash
  docker --version

2. Docker Compose

   Modern Docker sürümleriyle birlikte gelir.

   Test:

```docker-compose --version```

#### Kurulum Adımları

Aşağıdaki kodları windows cmd yada herhangi bir işletim sisteminin terminalinde çalıştırmanız gerekiyor

```
git clone https://github.com/eledhwen2004/toyota-project-2025.git
cd toyota-project-2025
docker-compose up --build
```


| Bileşen              | Port | Adres                                          |
| -------------------- | ---- | ---------------------------------------------- |
| Kafka UI             | 8082 | [http://localhost:8082](http://localhost:8082) |
| REST Rate API        | 8080 | [http://localhost:8080](http://localhost:8080) |
| TCP Platform         | 8081 | telnet localhost 8081                          |
| Ana Uygulama         | 8083 | [http://localhost:8083](http://localhost:8083) |
| OpenSearch Dashboard | 5601 | [http://localhost:5601](http://localhost:5601) |
| Kibana (Elastic)     | 5602 | [http://localhost:5602](http://localhost:5602) |

###  Mimari Bileşenler

    REST API Platform (toyota-rest-rate-api-platform)

        Spring Boot tabanlı

        /api/rates/{code} endpoint’i

    TCP Platform (toyota-tcp-rate-api-platform)

        Telnet üzerinden de çalışır

        Komut: subscribe|PF1_USDTRY

    Ana Uygulama (toyota-main-rate-api)

        Kafka Producer

        Hazelcast cache

        Dinamik subscriber yükleme

        Hesaplama motoru (Groovy)

    Kafka Consumer (kafka-consumer-application)

        PostgreSQL'e veri yazar

        Opensearch'e veri yollar

    Filebeat

        ./logs klasörünü izler

        Logları OpenSearch’e gönderir

###  Kafka Yapılandırması

    Topic: rates

    Producer: toyota-main-rate-api

    Consumer #1: PostgreSQL

    Consumer #2: OpenSearch (log)

    Kafka UI: http://localhost:8082


###  Loglama ve Gözlemlenebilirlik

    Log Seviyeleri: FATAL, ERROR, WARN, INFO, DEBUG, TRACE

    Log Formatı:

`` [Timestamp] [Level] [Platform] [RateName] [Message] ``

#### Yönlendirme:

    ./logs klasörüne yazılır

    Filebeat ile OpenSearch'e gönderilir

