package kr.kdev.otter.jwt;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.Instant;

@Accessors(chain = true)
@Data
public class TokenInfo {
    private String token;
    private Instant expiresAt;
}
