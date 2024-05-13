package account.api.security.dto;

public class PasswordUpdatedUiDto {
    private final String email;
    private final String status;

    public PasswordUpdatedUiDto(String email) {
        this.email = email;
        this.status = "The password has been updated successfully";
    }

    public String getEmail() {
        return email;
    }

    public String getStatus() {
        return status;
    }
}
