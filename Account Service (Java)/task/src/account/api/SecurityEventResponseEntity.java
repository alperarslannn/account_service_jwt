package account.api;

import account.api.security.CustomUserDetails;
import account.api.security.event.SecurityEventType;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Date;

public class SecurityEventResponseEntity<T> extends ResponseEntity<T> {
    private SecurityEventType eventName;
    private String path;
    private Date date;
    private Long subjectAccountId;
    private Long objectAccountId;
    private String object;

    public SecurityEventResponseEntity(T body, HttpStatusCode status) {
        super(body, status);
    }

    public SecurityEventType getEventName() {
        return eventName;
    }

    public void setEventName(SecurityEventType eventName) {
        this.eventName = eventName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Long getSubjectAccountId() {
        return subjectAccountId;
    }

    public void setSubjectAccountId(Long subjectAccountId) {
        this.subjectAccountId = subjectAccountId;
    }

    public Long getObjectAccountId() {
        return objectAccountId;
    }

    public void setObjectAccountId(Long objectAccountId) {
        this.objectAccountId = objectAccountId;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public static class Builder<T> {
        private T body;
        private HttpStatusCode status;
        private SecurityEventType eventName;
        private String path;
        private Date date;
        private Long objectAccountId;
        private String object;

        public Builder(T body, HttpStatusCode status) {
            this.body = body;
            this.status = status;
        }

        public Builder<T> eventName(SecurityEventType eventName) {
            this.eventName = eventName;
            return this;
        }

        public Builder<T> path(String path) {
            this.path = path;
            return this;
        }

        public Builder<T> date(Date date) {
            this.date = date;
            return this;
        }

        public Builder<T> objectAccountId(Long objectAccountId) {
            this.objectAccountId = objectAccountId;
            return this;
        }

        public Builder<T> object(String object) {
            this.object = object;
            return this;
        }

        public SecurityEventResponseEntity<T> build() {
            SecurityEventResponseEntity<T> securityEventResponseEntity = new SecurityEventResponseEntity<>(body, status);
            securityEventResponseEntity.setEventName(eventName);
            securityEventResponseEntity.setPath(path);
            securityEventResponseEntity.setDate(date);
            securityEventResponseEntity.setSubjectAccountId(SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals("anonymousUser") ? 0L:((CustomUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId());
            securityEventResponseEntity.setObjectAccountId(objectAccountId);
            securityEventResponseEntity.setObject(object);
            return securityEventResponseEntity;
        }
    }
}
