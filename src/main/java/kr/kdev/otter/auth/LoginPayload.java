package kr.kdev.otter.auth;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class LoginPayload {
    @NotEmpty
    private String id;
    @NotEmpty
    @Length(min = 6)
    private String passwd;
}
