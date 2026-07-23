package com.hsbc.deskflow;

public final class Admin {
    public static final long ID = 99L;
    public static final String NAME = "admin";

    private Admin() {
    }

    public static boolean isAdmin(Long employeeId) {
        return employeeId != null && employeeId == ID;
    }
}
