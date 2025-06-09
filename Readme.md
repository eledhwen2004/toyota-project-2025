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

docker-compose --version

#### Kurulum Adımları

```
git clone https://github.com/eledhwen2004/toyota-project-2025.git
cd toyota-project-2025
docker-compose up --build
```


| Bileşen              | Port | Adres                                          |
| -------------------- | ---- | ---------------------------------------------- |
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

## 🔍 OpenSearch Dashboards Üzerinden Log ve Rate Verilerinin İncelenmesi

Bu sistemde hem uygulama logları (Filebeat aracılığıyla) hem de hesaplanmış döviz kuru (rate) verileri OpenSearch'e yazılır. Bu veriler, **OpenSearch Dashboards** (veya Elasticsearch kullananlar için Kibana) üzerinde **Discover sekmesi** aracılığıyla kolayca görüntülenebilir.

---

### 🧾 A. Log Verilerini Görüntüleme (filebeat ile gelen)

#### 1. Index Pattern Oluştur
1. Tarayıcıda `http://localhost:5602` adresine git.
2. Sol menüden **“Stack Management” → “Index Patterns”** sekmesine gir.
3. Yeni bir index pattern oluştur:  
   **Pattern adı:** `filebeat-*`
4. Timestamp field olarak `@timestamp` seç.
5. Kaydet (Create Index Pattern).

#### 2. Logları Görüntüleme
1. Sol menüden **“Discover”** sekmesine tıkla.
2. Üst kısımdan `filebeat-*` index pattern'ini seç.
3. Sağ üstten tarih aralığını genişlet (örn. “Last 24 hours”).
4. Loglar liste halinde aşağıda görünür.

#### 3. Görüntülenebilecek Alanlar
- `@timestamp` → Log zamanı
- `log.level` → INFO, ERROR, WARN vb.
- `message` → Log içeriği
- `platform` → Logun hangi servis tarafından üretildiği
- `rateName`, `rateValue` gibi özel alanlar (log formatına bağlı olarak)

#### 🔎 Örnek Filtreler
```text
log.level: ERROR

rate_readme_content = """
## 💱 Rate Verilerini Görüntüleme (OpenSearch Dashboards - Discover)

Kafka Consumer bileşeni tarafından OpenSearch'e gönderilen döviz kuru (rate) verileri, Discover sekmesinde aşağıdaki adımlarla incelenebilir.
```


### 1. Index Pattern Oluştur

1. OpenSearch Dashboards arayüzüne `http://localhost:5601` adresinden giriş yapın.
2. Sol menüden **“Stack Management” → “Index Patterns”** sekmesine tıklayın.
3. **Yeni bir index pattern** oluşturun:
    - **Index pattern adı:** `rates-*` *(veya senin sistemine göre: `rate-data-*`, `logstash-*` olabilir)*
4. **Timestamp field** olarak `rateUpdatetime` veya `@timestamp` alanını seçin.
5. Kaydedin ve Discover ekranına geçin.

---

### 2. Discover Sekmesinden Rate Verilerini İnceleme

1. Sol menüde **Discover** sekmesine tıklayın.
2. Üst kısımdan `rates-*` index pattern’ini seçin.
3. Sağ üstten tarih aralığını “Last 15 minutes” veya “Last 1 hour” gibi geniş bir zaman aralığıyla ayarlayın.
4. Veri listesi otomatik olarak aşağıda görüntülenecektir.

---

### 3. Görüntülenebilecek Örnek Alanlar

| Alan Adı           | Açıklama                  |
|--------------------|---------------------------|
| `rateName`         | Örn: USDTRY               |
| `bid`              | Alış kuru (float)         |
| `ask`              | Satış kuru (float)        |
| `update_time`      | Verinin üretildiği zaman  |
| `dbUpdatetime`     | Veritabanına yazılma zamanı |

---

### 4. Örnek Filtreler

#### Belirli kur için:
```text
rateName: USDTRY
