package com.ou.journal.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ou.journal.service.interfaces.EditorFileService;
import com.ou.journal.service.interfaces.RenderPDFService;

@RestController
@RequestMapping("api/editor-files")
public class ApiEditorFileController {
    @Autowired
    private EditorFileService editorFileService;

    @Autowired
    private RenderPDFService renderPDFService;

    @GetMapping(path = "{editorFileId}")
    public ResponseEntity<?> getEditorFile(@PathVariable Long editorFileId) {
        try {
            return ResponseEntity.ok().body(editorFileService.retrieve(editorFileId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/view/{editorFileId}")
    public ResponseEntity<?> view(@PathVariable Long editorFileId) {
        try {
            return renderPDFService.viewEditorFile(editorFileId);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
