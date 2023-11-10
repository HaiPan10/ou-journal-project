package com.ou.journal.service.interfaces;

import com.ou.journal.pojo.EditorFile;

public interface EditorFileService {
    EditorFile retrieve(Long id) throws Exception;
}
