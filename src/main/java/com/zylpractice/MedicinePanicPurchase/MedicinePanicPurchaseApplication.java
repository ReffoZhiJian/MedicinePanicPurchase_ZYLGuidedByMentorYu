package com.zylpractice.MedicinePanicPurchase;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.mpp.mapper")
@SpringBootApplication
public class MedicinePanicPurchaseApplication {

    public static void main(String[] args) {
        SpringApplication.run(MedicinePanicPurchaseApplication.class, args);
    }

}
