package io.jumper.urlservice;

import org.springframework.boot.SpringApplication;

public class TestUrlServiceApplication {

    public static void main(String[] args) {
        SpringApplication.from(UrlserviceApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
