package com.ou.journal.service.impl;

import java.util.concurrent.ExecutorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.ou.journal.configs.JwtService;
import com.ou.journal.enums.ArticleStatus;
import com.ou.journal.enums.AuthorType;
import com.ou.journal.enums.ReviewArticleStatus;
import com.ou.journal.enums.RoleName;
import com.ou.journal.pojo.Account;
import com.ou.journal.pojo.Article;
import com.ou.journal.pojo.ArticleNote;
import com.ou.journal.pojo.MailRequest;
import com.ou.journal.pojo.ReviewArticle;
import com.ou.journal.pojo.User;
import com.ou.journal.repository.AuthorArticleRepositoryJPA;
import com.ou.journal.service.interfaces.MailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class MailServiceImpl implements MailService {
        @Autowired
        private JavaMailSender mailSender;

        @Autowired
        private TemplateEngine templateEngine;

        @Autowired
        @Qualifier("executorService")
        private ExecutorService executorService;

        @Autowired
        private Environment environment;

        @Autowired
        private JwtService jwtService;

        @Autowired
        private AuthorArticleRepositoryJPA authorArticleRepositoryJPA;

        @Override
        public void sendEmail(MailRequest mailRequest) {
                Runnable runnable = () -> {
                        MimeMessage mimeMessage = mailSender.createMimeMessage();
                        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");

                        try {
                                helper.setTo(mailRequest.getTo());
                                helper.setSubject(mailRequest.getSubject());
                                String htmlContent = templateEngine.process("mail/index", mailRequest.getContext());
                                helper.setText(htmlContent, true);
                                mailSender.send(mimeMessage);
                        } catch (MessagingException e) {
                                // Handle exception
                        }
                };
                executorService.execute(runnable);
        }

        @Override
        public void sendVerificationMail(Account account) {
                Context context = new Context();
                String subject = "Xác thực email";
                String body = "OU JOURNAL đã nhận được yêu cầu tạo tài khoản của bạn. Vui lòng nhấn vào nút bên dưới để xác thực email!";
                context.setVariable("subject", subject);
                context.setVariable("body", body);
                // context.setVariable("actionLink", String.format("%s/api/mail/verify/%s/%s",
                // environment.getProperty("SERVER_HOSTNAME"), account.getId(),
                // account.getVerificationCode()));
                context.setVariable("actionName", "Xác thực ngay");
                MailRequest mailRequest = new MailRequest(account.getEmail(), subject, body, context);
                sendEmail(mailRequest);
        }

        @Override
        public void sendInvitationMail(ReviewArticle reviewArticle) {
                Context context = new Context();
                String subject = "Thư mời review bài báo";
                String body = String.format("Xin chào %s! Bạn có một lời mời phản biện bài báo từ OU Journal."
                                + " Để biết thêm thông tin chi tiết, vui lòng liên hệ cho biên tập viên của bài báo này thông qua email %s.",
                                reviewArticle.getUser().getFirstName(),
                                reviewArticle.getManuscript().getArticle().getEditorUser().getEmail());
                context.setVariable("subject", subject);
                context.setVariable("body", body);
                String token = jwtService.generateReviewerInvitationToken(reviewArticle.getUser(), reviewArticle);
                context.setVariable("firstActionLink",
                                String.format("%s/api/review-articles/response?status=%s&token=%s",
                                                environment.getProperty("SERVER_HOSTNAME"),
                                                ReviewArticleStatus.ACCEPTED.toString(), token));
                context.setVariable("firstActionName", "Đồng ý");
                context.setVariable("secondActionLink",
                                String.format("%s/api/review-articles/response?status=%s&token=%s",
                                                environment.getProperty("SERVER_HOSTNAME"),
                                                ReviewArticleStatus.REJECTED.toString(), token));
                context.setVariable("secondActionName", "Từ chối");
                MailRequest mailRequest = new MailRequest(reviewArticle.getUser().getEmail(), subject, body, context);
                sendEmail(mailRequest);
        }

        @Override
        public void sendSecretaryVerificationmail(Article article, ArticleNote articleNote) {
                Context context = new Context();
                String subject = "Thông báo trạng thái bài báo";
                String articleAction = (article.getStatus().equals(ArticleStatus.ASSIGN_EDITOR.toString())
                                ? " được chấp thuận và đang tiến hành chờ biên tập viên."
                                : " bị từ chối.");
                String secretaryNote = (articleNote.getNote().length() != 0
                                ? " Nhận xét từ thư ký: " + articleNote.getNote() + "."
                                : "");
                String body = String.format("Phía thư ký đã duyệt bài báo của bạn. Bài báo của bạn đã %s%s" +
                                " Bạn có thể truy cập vào trang web để theo dõi trạng thái bài đăng hoặc rút bài khi nhấn vào đường link phía dưới.",
                                articleAction, secretaryNote);

                context.setVariable("subject", subject);
                context.setVariable("body", body);

                User correspondingUser = authorArticleRepositoryJPA
                                .findByAnyRoleInAuthourArticle(article.getId(),
                                                AuthorType.CORRESPONDING_AUTHOR.toString())
                                .get();
                String targetEndpoint = String.format("submission/processing/%s", String.valueOf(article.getId()));
                String token = jwtService.generateMailLoginToken(correspondingUser, "articleId", article.getId(),
                                RoleName.ROLE_AUTHOR.toString(), targetEndpoint);
                context.setVariable("firstActionLink", String.format("%s/api/accounts/login?token=%s",
                                environment.getProperty("SERVER_HOSTNAME"), token));
                context.setVariable("firstActionName", "Theo dõi trạng thái");
                // context.setVariable("secondActionLink",
                // String.format("%s/api/articles/author/article/withdraw?token=%s",
                // environment.getProperty("SERVER_HOSTNAME"), token));
                // context.setVariable("secondActionName", "Rút bài");
                MailRequest mailRequest = new MailRequest(correspondingUser.getEmail(), subject, body, context);
                sendEmail(mailRequest);
        }

        @Override
        public void sendCreateAccountMail(User user) {
                Context context = new Context();
                String subject = "Thư mời tạo tài khoản";
                String body = "OU JOURNAL gửi cho bạn đường link bên dưới, nhấn vào để xác thực tài khoản!";
                context.setVariable("subject", subject);
                context.setVariable("body", body);
                String token = jwtService.generateToken(user);
                context.setVariable("firstActionLink",
                                String.format("%s/api/accounts/create?token=%s",
                                                environment.getProperty("SERVER_HOSTNAME"), token));
                context.setVariable("firstActionName", "Đồng ý");
                MailRequest mailRequest = new MailRequest(user.getEmail(), subject, body, context);
                sendEmail(mailRequest);
        }

        @Override
        public void sendInReviewStatusChangeMail(Article article) {
                User correspondingUser = authorArticleRepositoryJPA
                                .findByAnyRoleInAuthourArticle(article.getId(),
                                                AuthorType.CORRESPONDING_AUTHOR.toString())
                                .get();
                Context context = new Context();
                String subject = "Thông báo trạng thái bài báo";
                String body = String.format(
                                "Xin chào %s %s! Bài báo của bạn đã có đủ reviewer và đang tiến vào giai đoạn review. Truy cập vào hệ thống để theo dõi trạng thái bài báo!"
                                                +
                                                " Để rút bài báo này, nhấn vào tùy chọn rút bài!",
                                correspondingUser.getLastName(), correspondingUser.getFirstName());
                context.setVariable("subject", subject);
                context.setVariable("body", body);
                String token = jwtService.generateMailLoginToken(correspondingUser, "articleId", article.getId(),
                                RoleName.ROLE_AUTHOR.toString(), "homepage");
                context.setVariable("firstActionLink", String.format("%s", environment.getProperty("SERVER_HOSTNAME")));
                context.setVariable("firstActionName", "Theo dõi trạng thái");
                // context.setVariable("secondActionLink",
                // String.format("%s/api/articles/author/article/withdraw?token=%s",
                // environment.getProperty("SERVER_HOSTNAME"), token));
                // context.setVariable("secondActionName", "Rút bài");
                MailRequest mailRequest = new MailRequest(correspondingUser.getEmail(), subject, body, context);
                sendEmail(mailRequest);
        }

        // @Override
        // public void sendDecidingArticleEmail(Article article) {
        // System.out.println("[DEBUG] - SEND DECIDING EMAIL TO EDITOR AND AUTHOR");
        // User correspondingUser = authorArticleRepositoryJPA
        // .findByAnyRoleInAuthourArticle(article.getId(),
        // AuthorType.CORRESPONDING_AUTHOR.toString()).get();
        // System.out.println("CORS USER " + correspondingUser.getEmail());
        // Context context = new Context();
        // String subject = "Thông báo trạng thái bài báo";
        // String body = String.format(
        // "Xin chào %s %s! Bài báo của bạn đã review xong và đang tiến vào giai đoạn
        // biên tập viên quyết định. Truy cập vào hệ thống để theo dõi trạng thái bài
        // báo!",
        // correspondingUser.getLastName(), correspondingUser.getFirstName());
        // context.setVariable("subject", subject);
        // context.setVariable("body", body);
        // context.setVariable("firstActionLink", String.format("%s",
        // environment.getProperty("SERVER_HOSTNAME")));
        // context.setVariable("firstActionName", "Theo dõi trạng thái");
        // MailRequest mailRequest = new MailRequest(correspondingUser.getEmail(),
        // subject, body, context);
        // sendEmail(mailRequest);

        // // User editor = userService.findByEmail("editor")
        // }

        @Override
        public void sendAssignEditorMail(Article article) {
                User assignedEditor = article.getEditorUser();
                System.out.println("[DEBUG] - EDITOR USER: " + assignedEditor.getEmail());
                Context context = new Context();
                String subject = "Thông báo bài viết cho biên tập viên";
                String body = String.format(
                                "Xin chào %s %s! Bạn được tổng biên tập viên mời vào bài báo mới. Truy cập vào hệ thống để theo dõi trạng thái bài báo!",
                                assignedEditor.getLastName(), assignedEditor.getFirstName());
                context.setVariable("subject", subject);
                context.setVariable("body", body);
                context.setVariable("firstActionLink", String.format("%s", environment.getProperty("SERVER_HOSTNAME")));
                context.setVariable("firstActionName", "Theo dõi bài báo được gán");
                MailRequest mailRequest = new MailRequest(assignedEditor.getEmail(), subject, body, context);
                sendEmail(mailRequest);
        }

        @Override
        public void sendDecideEmail(Article article) {
                User correspondingUser = authorArticleRepositoryJPA
                                .findByAnyRoleInAuthourArticle(article.getId(),
                                                AuthorType.CORRESPONDING_AUTHOR.toString())
                                .get();
                
                Context context = new Context();
                String subject = "Thông báo trạng thái bài báo";
                String body = String.format("Xin chào %s %s! Bài báo của bạn đã được quyết định. " +
                                " Bạn có thể truy cập vào trang web để theo dõi trạng thái bài đăng hoặc nhấn vào đường link phía dưới.",
                                correspondingUser.getLastName(), correspondingUser.getFirstName());

                context.setVariable("subject", subject);
                context.setVariable("body", body);


                String targetEndpoint = String.format("submission/processing/%s", String.valueOf(article.getId()));
                String token = jwtService.generateMailLoginToken(correspondingUser, "articleId", article.getId(),
                                RoleName.ROLE_AUTHOR.toString(), targetEndpoint);
                context.setVariable("firstActionLink", String.format("%s/api/accounts/login?token=%s",
                                environment.getProperty("SERVER_HOSTNAME"), token));
                context.setVariable("firstActionName", "Theo dõi trạng thái");
                MailRequest mailRequest = new MailRequest(correspondingUser.getEmail(), subject, body, context);
                sendEmail(mailRequest);

        }
}