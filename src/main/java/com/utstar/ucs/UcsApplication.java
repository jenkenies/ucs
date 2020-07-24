package com.utstar.ucs;

import com.utstar.ucs.annotation.EnableSecurity;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableSecurity
@SpringBootApplication
public class UcsApplication {

    public static void main(String[] args) {
        SpringApplication.run(UcsApplication.class, args);
    }

}
