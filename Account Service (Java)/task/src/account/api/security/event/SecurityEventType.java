package account.api.security.event;

public enum SecurityEventType {
    CREATE_USER("A user has been successfully registered", "CREATE_USER"),
    CHANGE_PASSWORD("A user has changed the password successfully", "CHANGE_PASSWORD"),
    ACCESS_DENIED("A user is trying to access a resource without access rights", "ACCESS_DENIED"),
    LOGIN_FAILED("Failed authentication", "LOGIN_FAILED"),
    GRANT_ROLE("A role is granted to a user", "GRANT_ROLE"),
    REMOVE_ROLE("A role has been revoked", "REMOVE_ROLE"),
    LOCK_USER("The Administrator has locked the user", "LOCK_USER"),
    UNLOCK_USER("The Administrator has unlocked a user", "UNLOCK_USER"),
    DELETE_USER("The Administrator has deleted a user", "DELETE_USER"),
    BRUTE_FORCE("A user has been blocked on suspicion of a brute force attack", "BRUTE_FORCE");


    private final String description;
    private final String eventName;

    SecurityEventType(String description, String eventName) {
        this.description = description;
        this.eventName = eventName;
    }

    public String getDescription() {
        return description;
    }

    public String getEventName() {
        return eventName;
    }

}

