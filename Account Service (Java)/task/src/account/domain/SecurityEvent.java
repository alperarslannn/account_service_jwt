package account.domain;

import account.api.security.event.SecurityEventType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import java.util.Date;

@Entity
@Table(name = "security_events")
public class SecurityEvent {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "security_event_seq")
    @SequenceGenerator(name = "security_event_seq", sequenceName = "SECURITY_EVENT_SEQ", allocationSize = 1)
    private Long id;
    @Column(name = "SECURITY_EVENT_NAME")
    @Enumerated(EnumType.STRING)
    private SecurityEventType eventName;
    @Column
    private String path;
    @Column
    private Date date;
    @Column
    private Long subjectAccountId;
    @Column
    private Long objectAccountId;
    @Column
    private String object;

    public Long getId() {
        return id;
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
}