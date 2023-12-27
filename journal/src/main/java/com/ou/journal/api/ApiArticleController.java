package com.ou.journal.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ou.journal.configs.JwtService;
import com.ou.journal.pojo.Article;
import com.ou.journal.pojo.ArticleNote;
import com.ou.journal.pojo.AuthenticationUser;
import com.ou.journal.pojo.AuthorNote;
import com.ou.journal.service.interfaces.ArticleService;
import com.ou.journal.service.interfaces.ManuscriptService;
import com.ou.journal.service.interfaces.RenderPDFService;

@RestController
@RequestMapping("/api/articles")
public class ApiArticleController {
    @Autowired
    private ArticleService articleService;

    @Autowired
    private RenderPDFService renderPDFService;

    // @Autowired
    // private UserRoleService userRoleService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private Environment environment;

    @Autowired
    private ManuscriptService manuscriptService;

    // @Autowired
    // private UserService userService;

    @GetMapping("/view/{articleId}")
    public ResponseEntity<byte[]> view(@PathVariable Long articleId, @RequestParam(required = false) String version) {
        try {
            return renderPDFService.viewArticle(articleId, version);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/view-anonymous/{articleId}")
    public ResponseEntity<byte[]> viewAnonymous(@PathVariable Long articleId, @RequestParam(required = false) String version) {
        try {
            return renderPDFService.viewAnonymous(articleId, version);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/view-appendix/{articleId}")
    public ResponseEntity<byte[]> viewAppendix(@PathVariable Long articleId, @RequestParam(required = false) String version) {
        try {
            return renderPDFService.viewAppendix(articleId, version);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Secured("ROLE_EDITOR")
    @PostMapping("/editor/decide/{articleId}")
    public ResponseEntity<?> decideArticle(@PathVariable Long articleId, List<MultipartFile> decideFiles,
     String status, String note, @AuthenticationPrincipal AuthenticationUser currentUser) throws Exception {
        try {
            // System.out.println("STATUS: " + status);
            // System.out.println("NOTE: " + note);
            // decideFiles.forEach(decideFile -> {
            //     System.out.println("FILES NAME: " + decideFile.getOriginalFilename());
            // });
            // return ResponseEntity.badRequest().body("NCC");
            return ResponseEntity.ok().body(articleService.editorDecide(articleId, status, new ArticleNote(note), decideFiles));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Secured("ROLE_AUTHOR")
    @PutMapping("/author/article/withdraw/{articleId}")
    public ResponseEntity<?> withdrawArticle(@PathVariable Long articleId,
            @AuthenticationPrincipal AuthenticationUser currentUser) throws Exception {
        try {
            return ResponseEntity.ok().body(articleService.widthdrawArticle(articleId,
                    currentUser.getId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // @Secured("ROLE_AUTHOR")
    // @GetMapping("/author/article/withdraw")
    // public ResponseEntity<?> withdrawArticle(@RequestParam String token) throws Exception {
    //     try {
    //         Long id = jwtService.getUserIdFromToken(token, SecrectType.EMAIL);
    //         Long articleId = jwtService.getArticleIdFromToken(token, SecrectType.EMAIL);

    //         articleService.widthdrawArticle(articleId, id);

    //         HttpHeaders headers = new HttpHeaders();
    //         headers.setLocation(URI.create(String.format("%s/article-action/success?token=%s",
    //                 environment.getProperty("SERVER_HOSTNAME"), token)));
    //         return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
    //     } catch (Exception e) {
    //         HttpHeaders headers = new HttpHeaders();
    //         headers.setLocation(URI.create(String.format("%s/article-action/failed?token=%s&reason=%s",
    //                 environment.getProperty("SERVER_HOSTNAME"), token, e.getMessage())));
    //         return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
    //     }
    // }

    @Secured("ROLE_EDITOR")
    @PutMapping(value = "/editor/assign/{articleId}")
    public ResponseEntity<?> assignEditor(@AuthenticationPrincipal AuthenticationUser currentUser,
            @PathVariable Long articleId) {
        try {
            articleService.assignEditor(articleId, currentUser.getId());
            return ResponseEntity.ok().body("Tự gán biên tập viên cho bài báo thành công!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Secured("ROLE_CHIEF_EDITOR")
    @PutMapping(value = "/chief-editor/assign")
    public ResponseEntity<?> assignEditor(@RequestBody Article article) {
        try {
            System.out.println("[DEBUG] - ARTICLE: " + article.getEditorUser().getEmail());
            articleService.assignEditor(article.getId(), article.getEditorUser().getEmail());
            return ResponseEntity.ok().body("Gán biên tập viên cho bài báo thành công!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // @Secured("ROLE_CHIEF_EDITOR")
    // @PutMapping(value = "/chief-editor/unassign")

    @Secured("ROLE_AUTHOR")
    @PostMapping(path = "/resubmit/{articleId}")
    public ResponseEntity<?> reSubmitManuscript(@PathVariable Long articleId,
            @RequestPart("file") MultipartFile file, @RequestPart("file-anonymous") MultipartFile fileAnonymous,
            @RequestPart("file-appendix") MultipartFile fileAppendix, String reference, String note) {
        try {
            return ResponseEntity.ok()
                    .body(manuscriptService.reUpManuscript(articleId, file, fileAnonymous, fileAppendix, reference,
                            new AuthorNote(note)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Secured("ROLE_EDITOR")
    @PutMapping("/anonymous/{articleId}")
    public ResponseEntity<?> reSubmitAnonymousFile(@PathVariable Long articleId, MultipartFile anonymousFile) {
        try {
            return ResponseEntity.ok()
                    .body(manuscriptService.updateAnonymousFile(anonymousFile, articleId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
