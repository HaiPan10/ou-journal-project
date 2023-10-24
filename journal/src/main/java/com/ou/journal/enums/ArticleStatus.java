package com.ou.journal.enums;

public enum ArticleStatus {
    PENDING("Chờ duyệt"),
	SECRETARY_REJECT("Thư ký từ chối"),
    INVITING_REVIEWER("Chờ mời reviewer"),
	IN_REVIEW("Đang review"),
    DECIDING("Chờ quyết định"),
    ACCEPT("Được duyệt"),
    REJECT("Từ chối"),
    WITHDRAW("Đã rút bài"),
    PUBLIC("Xuất bản");

    private final String displayName;
    private ArticleStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}