package kr.kdev.otter.auth;

import kr.kdev.otter.jwt.JwtFactory;
import kr.kdev.otter.jwt.JwtToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/jwt")
class JwtLoginApi {

    private final JwtFactory jwtFactory;

    @PostMapping("/login")
    public JwtToken login(@Validated @RequestBody LoginPayload loginPayload) {
        log.info("{}", loginPayload); // NOTE: Do not print rawPassword in production.
        return jwtFactory.generateToken(loginPayload.getId());
    }
}
