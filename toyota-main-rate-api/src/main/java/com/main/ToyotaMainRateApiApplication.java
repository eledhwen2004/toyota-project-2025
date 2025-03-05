package com.main;

import com.main.Coordinator.Coordinator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.io.IOException;

@SpringBootApplication
public class ToyotaMainRateApiApplication {

    public static void main(String[] args) throws IOException {
        Coordinator coordinator = new Coordinator((ApplicationContext) SpringApplication.run(ToyotaMainRateApiApplication.class, args));
    }

}
