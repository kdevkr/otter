package kr.kdev.otter.mail;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MailSendingLogHandler {
    @Async
    @EventListener(SendFailedMail.class)
    public void handle(SendFailedMail failedMail) {
        log.error("to: {}, failReason: {}", failedMail.getWaitingMail().getTo(), failedMail.getFailReason());
        // TODO: Store history of sending failures in database or log storage
    }
}
