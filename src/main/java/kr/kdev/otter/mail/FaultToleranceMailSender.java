package kr.kdev.otter.mail;

import com.google.common.util.concurrent.RateLimiter;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.env.Environment;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.concurrent.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class FaultToleranceMailSender implements InitializingBean {
    private static final BlockingQueue<WaitingMail> waitingQueue = new LinkedBlockingQueue<>(1000);

    private final JavaMailSenderImpl javaMailSender;
    private final Environment environment;
    private final ApplicationEventPublisher applicationEventPublisher;

    private RateLimiter rateLimiter = RateLimiter.create(10);
    private ThreadPoolExecutor executor;
    private Date throttlingFailureAfterReleaseAt;
    private String from;

    @Override
    public void afterPropertiesSet() throws Exception {
        this.from = environment.getProperty("spring.mail.from", String.class);
        Integer sendingRate = environment.getProperty("spring.mail.sending-rate", Integer.class);
        if (sendingRate != null) {
            this.rateLimiter = RateLimiter.create(sendingRate);
        }
        int coreSize = Runtime.getRuntime().availableProcessors();
        ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("Mail-Thread-%d").build();
        this.executor = new ThreadPoolExecutor(coreSize, coreSize, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<>(1000), threadFactory);

        consumption();
    }

    public boolean sendMail(String to, String subject, String text, boolean debug) {
        WaitingMail waitingMail = WaitingMail.create().setTo(to).setSubject(subject).setText(text).setDebug(debug);

        try {
            waitingQueue.add(waitingMail);
            return true;
        } catch (IllegalStateException e) {
            // Dealing with sending failure when mail queues exceed
            if (e.getMessage().contains("Queue full")) {
                SendFailedMail sendFailedMail = new SendFailedMail()
                        .setWaitingMail(waitingMail)
                        .setFailReason("Mail queue exceeded")
                        .setSentDate(Date.from(Instant.now()));
                applicationEventPublisher.publishEvent(sendFailedMail);
            }
            return false;
        }
    }

    private void consumption() {
        Thread thread = new Thread(() -> {
            while (true) {
                if (waitingQueue.isEmpty() || !tryAcquire()) {
                    continue;
                }

                try {
                    WaitingMail waitingMail = waitingQueue.take();
                    executor.execute(() -> {
                        Date sentDate = Date.from(Instant.now());

                        try {
                            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
                            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
                            mimeMessageHelper.setTo(waitingMail.getTo());
                            mimeMessageHelper.setSubject(waitingMail.getSubject());
                            mimeMessageHelper.setText(waitingMail.getText(), true);
                            mimeMessageHelper.setFrom(from);
                            if (waitingMail.isDebug()) {
                                log.debug("[Debug] Not send for debug to: {}", waitingMail.getTo());
                                throw new MailSendException("454 Throttling failure: Maximum sending rate exceeded");
                            } else {
                                javaMailSender.send(mimeMessage);
                            }
                        } catch (MailException | MessagingException e) {
                            String failReason = e.getMessage();
                            if (failReason != null && failReason.contains("454 Throttling failure")) {
                                // NOTE: Stop sending mails according to the sending limit.
                                transitionOpenState();
                            }

                            SendFailedMail sendFailedMail = new SendFailedMail()
                                    .setWaitingMail(waitingMail)
                                    .setFailReason(failReason)
                                    .setSentDate(sentDate);

                            applicationEventPublisher.publishEvent(sendFailedMail);
                        }
                    });
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        thread.setName("Mail-Thread");
        thread.setDaemon(true);
        thread.start();
    }

    private synchronized void transitionOpenState() {
        throttlingFailureAfterReleaseAt = Date.from(Instant.now().plus(10L, ChronoUnit.MINUTES));
    }

    private boolean tryAcquire() {
        if (throttlingFailureAfterReleaseAt == null) {
            return rateLimiter.tryAcquire();
        } else if (Date.from(Instant.now()).after(throttlingFailureAfterReleaseAt)) {
            throttlingFailureAfterReleaseAt = null; // NOTE: transitionClosedState
            return rateLimiter.tryAcquire();
        }
        return false;
    }

    @Scheduled(fixedRateString = "PT1M")
    public void reportThrottlingFailureState() {
        if (throttlingFailureAfterReleaseAt != null) {
            log.debug("[Report] Release open state after {}", throttlingFailureAfterReleaseAt.toInstant());
        }
    }

}
