package kr.kdev.otter.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@ConfigurationProperties(prefix = "jwt")
@AllArgsConstructor
@Getter
public class JwtProperties {
    private String secret;
    @DurationUnit(ChronoUnit.SECONDS)
    private Duration expires;
    @DurationUnit(ChronoUnit.SECONDS)
    private Duration refreshExpires;
}
