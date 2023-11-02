package com.ou.journal.enums;

public enum RoleName {
    ROLE_ADMIN("Quản trị viên"),
    ROLE_EDITOR ("Biên tập viên"),
    ROLE_REVIEWER ("Phản biện viên"),
    ROLE_AUTHOR ("Tác giả"),
    ROLE_SECRETARY ("Thư ký"),
    ROLE_CHIEF_EDITOR("Tổng biên tập viên");

    private final String displayName;

    private RoleName(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}