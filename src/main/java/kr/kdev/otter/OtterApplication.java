package kr.kdev.otter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@ConfigurationPropertiesScan
@EnableJpaRepositories
@EnableJpaAuditing
@SpringBootApplication
public class OtterApplication {

    public static void main(String[] args) {
        SpringApplication.run(OtterApplication.class, args);
    }

}
