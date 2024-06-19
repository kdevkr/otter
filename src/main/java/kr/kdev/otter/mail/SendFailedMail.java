package kr.kdev.otter.mail;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Accessors(chain = true)
@Data
public class SendFailedMail {
    private WaitingMail waitingMail;
    private String failReason;
    private Date sentDate;
}
