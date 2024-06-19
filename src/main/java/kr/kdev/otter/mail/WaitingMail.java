package kr.kdev.otter.mail;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.UUID;

@Accessors(chain = true)
@Data
public class WaitingMail {
    private String id;
    private String to;
    private String subject;
    private String text;
    private boolean debug;

    public static WaitingMail create() {
        return new WaitingMail().setId(UUID.randomUUID().toString());
    }
}