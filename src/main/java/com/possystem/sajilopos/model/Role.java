package com.possystem.sajilopos.model;


public enum Role {
    ADMIN("ADMIN", 1),
    MANAGER("MANAGER", 2),
    CASHIER("CASHIER", 3);

    private final String roleName;
    private final int roleId;

    Role(String roleName, int roleId) {
        this.roleName = roleName;
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public int getRoleId() {
        return roleId;
    }

    
    public static Role fromName(String name) {
        for (Role role : Role.values()) {
            if (role.roleName.equalsIgnoreCase(name)) {
                return role;
            }
        }
        return null;
    }

    
    public static Role fromId(int id) {
        for (Role role : Role.values()) {
            if (role.roleId == id) {
                return role;
            }
        }
        return null;
    }

    
    public boolean hasPermission(String operation) {
        switch (this) {
            case ADMIN:
                return true; // Admin has all permissions
            case MANAGER:
                return !operation.equals("MANAGE_USERS") && !operation.equals("SYSTEM_CONFIG");
            case CASHIER:
                return operation.equals("PROCESS_SALES") || operation.equals("VIEW_PRODUCTS");
            default:
                return false;
        }
    }
}
