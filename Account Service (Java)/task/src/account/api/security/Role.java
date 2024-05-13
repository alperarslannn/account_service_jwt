package account.api.security;

import java.util.List;

public enum Role {
    ADMINISTRATOR, USER, ACCOUNTANT, AUDITOR;

    public static List<Role> getRoles() {
        return List.of(Role.values());
    }
    public static List<Role> getBusinessRoles() {
        return List.of(USER, ACCOUNTANT, AUDITOR);
    }

    public static Role findRoleByAuthorityName(String authorityName) {
        return switch (authorityName) {
            case "ROLE_ADMINISTRATOR" -> Role.ADMINISTRATOR;
            case "ROLE_USER" -> Role.USER;
            case "ROLE_ACCOUNTANT" -> Role.ACCOUNTANT;
            case "ROLE_AUDITOR" -> Role.AUDITOR;
            default -> throw new IllegalArgumentException("Unknown authority name: " + authorityName);
        };
    }

    public static String getAuthorityNameByRole(Role role) {
        return "ROLE_" + role.name();
    }
}
