package account.api.security.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Length;

public class NewPasswordUiDto {
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Length(min = 12, message = "Password length must be 12 chars minimum!")
    private String new_password;

    public String getNew_password() {
        return new_password;
    }
}
