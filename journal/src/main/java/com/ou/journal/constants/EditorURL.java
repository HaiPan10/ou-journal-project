package com.ou.journal.constants;

import java.util.HashMap;
import java.util.Map;

public class EditorURL {
    public static final Map<String, String[]> BACK_MAP = createBackMap();

    private static Map<String, String[]> createBackMap() {
        Map<String, String[]> backMap = new HashMap<>();
        backMap.put("assign-list", new String[] {"/editor/assign-list", "Danh sách bài báo chờ gán biên tập viên"});
        backMap.put("assigned-list", new String[] {"/editor/assigned-list", "Danh sách bài báo được gán biên tập viên"});
        backMap.put("invite-reviewer-articles", new String[] {"/editor/invite-reviewer-articles", "Danh sách bài báo chờ mời phản biện viên"});
        backMap.put("waiting-accept-reviewer-articles", new String[] {"/editor/waiting-accept-reviewer-articles", "Danh sách bài báo chờ chưa có phản biện viên"});
        backMap.put("in-review-articles", new String[] {"/editor/in-review-articles", "Danh sách bài báo đang được phản biện"});
        backMap.put("reviewed-articles", new String[] {"/editor/reviewed-articles", "Danh sách bài báo đã phản biện"});
        return backMap;
    }
}
