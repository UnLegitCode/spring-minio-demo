package ru.unlegit.miniodemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import ru.unlegit.miniodemo.configuration.MinIoConfiguration;

@SpringBootApplication
@EnableConfigurationProperties(MinIoConfiguration.MinIoProperties.class)
public class MinioDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(MinioDemoApplication.class, args);
    }
}