package com.ou.journal.enums;

public enum ArticleStatus {
    PENDING("Chờ duyệt", 1),
	SECRETARY_REJECT("Thư ký từ chối", 1),
    ASSIGN_EDITOR("Chờ gán biên tập viên", 1),
    INVITING_REVIEWER("Chờ mời reviewer", 1),
	IN_REVIEW("Đang review", 2),
    DECIDING("Chờ quyết định", 3),
    ACCEPT("Được duyệt", 3),
    REJECT("Từ chối", 3),
    WITHDRAW("Đã rút bài", 4),
    PUBLIC("Xuất bản", 4);

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