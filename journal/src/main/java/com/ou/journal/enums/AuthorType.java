package com.ou.journal.enums;

public enum AuthorType {
    FIRST_AUTHOR(1, "Tác giả chính"),
    CORRESPONDING_AUTHOR(2, "Tác giả liên hệ"), 
    AUTHOR(3, "Tác giả");

    private final int id;
    private final String displayName;

    private AuthorType(int id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    public int getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }
}
