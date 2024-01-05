package com.ou.journal.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ou.journal.pojo.Article;
import com.ou.journal.service.interfaces.ArticleService;

@RestController
public class ApiSubmitController {

    @Autowired
    private ArticleService articleService;

    @PostMapping({ "/complete-submit" })
    public ResponseEntity<?> complete(@RequestParam(name = "back", required = false) String back,
            @ModelAttribute("article") Article article,
            @RequestPart("file") MultipartFile file,
            @RequestPart("anonymousFile") MultipartFile anonymousFile,
            @RequestPart(name = "appendixFile", required = false) MultipartFile appendixFile,
            RedirectAttributes redirectAttrs,
            SessionStatus sessionStatus) {
        if (back != null) {
            return new ResponseEntity<>("submit/step5", HttpStatus.SEE_OTHER);
        }
        if (file != null && file.getSize() > 0 && anonymousFile != null && anonymousFile.getSize() > 0) {
            try {
                Article persistArticle = articleService.create(article, file, anonymousFile, appendixFile);
                redirectAttrs.addFlashAttribute("successMsg", "Gửi bài thành công");

                sessionStatus.setComplete();

                return new ResponseEntity<>(persistArticle.getId(), HttpStatus.CREATED);
            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<>("Có lỗi xảy ra: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<>("Yêu cầu không hợp lệ", HttpStatus.BAD_REQUEST);

    }
}
