package kr.kdev.otter.mail;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.mail.MailHealthIndicator;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Component("mailHealthIndicator")
public class CachableMailHealthIndicator extends MailHealthIndicator implements InitializingBean {
    private Cache<String, Health> cache;

    public CachableMailHealthIndicator(JavaMailSenderImpl javaMailSender) {
        super(javaMailSender);
    }

    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {
        Health cached = cache != null ? cache.getIfPresent("mail") : null;
        if (cached != null) {
            builder.status(cached.getStatus()).withDetails(cached.getDetails());
            return;
        }

        try {
            super.doHealthCheck(builder);
        } catch (Exception e) {
            builder.down(e);
        }

        if (cache != null) {
            Health health = builder.withDetail("datetime", Instant.now()).build();
            cache.put("mail", health);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        cache = CacheBuilder.newBuilder()
                .maximumSize(1)
                .expireAfterWrite(Duration.ofSeconds(30))
                .build();
    }
}
