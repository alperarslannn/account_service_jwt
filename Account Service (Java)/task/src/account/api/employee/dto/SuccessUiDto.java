package account.api.employee.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SuccessUiDto {
    private String status;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String user;
    @JsonIgnore
    private Long id;

    public SuccessUiDto(String status) {
        this.status = status;
    }

    public SuccessUiDto(String status, String email, Long id) {
        this.status = status;
        this.user = email;
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public String getUser() {
        return user;
    }

    public Long getId() {
        return id;
    }
}
