package account.api.security.dto;

import java.util.Date;

public class SecurityEventUiDto {
    private Date date;
    private String action;
    private String subject;
    private String object;
    private String path;

    public SecurityEventUiDto() {
    }

    public SecurityEventUiDto(Date date, String action, String subject, String object, String path) {
        this.date = date;
        this.action = action;
        this.subject = subject;
        this.object = object;
        this.path = path;
    }

    public Date getDate() {
        return date;
    }

    public String getAction() {
        return action;
    }

    public String getSubject() {
        return subject;
    }

    public String getObject() {
        return object;
    }

    public String getPath() {
        return path;
    }
}
