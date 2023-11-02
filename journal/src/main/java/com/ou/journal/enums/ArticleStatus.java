package com.ou.journal.enums;

public enum ArticleStatus {
    PENDING("Chờ duyệt", 1),
	SECRETARY_REJECT("Thư ký từ chối", 1),
    ASSIGN_EDITOR("Chờ gán biên tập viên", 2),
    INVITING_REVIEWER("Đang mời reviewer", 3),
	IN_REVIEW("Đang review", 3),
    // DECIDING("Chờ quyết định", 3),
    ACCEPT("Được duyệt", 4),
    REJECT("Từ chối", 4),
    WITHDRAW("Đã rút bài", -1),
    PUBLIC("Xuất bản", 5);

    private final String displayName;
    private final int stage;
    
    private ArticleStatus(String displayName, int stage) {
        this.displayName = displayName;
        this.stage = stage;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getStage() {
        return stage;
    }
}