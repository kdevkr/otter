package kr.kdev.otter.jwt;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.Instant;

@Accessors(chain = true)
@Data
public class JwtToken {
    private String type = "Bearer";
    private String accessToken;
    private String refreshToken;
    private Instant expireAt;
    private Instant refreshExpireAt;
}
