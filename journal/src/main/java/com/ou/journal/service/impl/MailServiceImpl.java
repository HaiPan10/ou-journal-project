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
import com.ou.journal.pojo.Account;
import com.ou.journal.pojo.MailRequest;
import com.ou.journal.pojo.ReviewArticle;
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
        context.setVariable("actionLink", String.format("%s/api/mail/verify/%s/%s", environment.getProperty("SERVER_HOSTNAME") ,account.getId(), account.getVerificationCode()));
        context.setVariable("actionName", "Xác thực ngay");
        MailRequest mailRequest = new MailRequest(account.getEmail(), subject, body, context);
        sendEmail(mailRequest);
    }

    @Override
    public void sendInvitationMail(ReviewArticle reviewArticle) {
        Context context = new Context();
        String subject = "Thư mời review bài báo";
        String body = "OU JOURNAL kính mời bạn review cho bài báo của chúng tôi!";
        context.setVariable("subject", subject);
        context.setVariable("body", body);
        String token = jwtService.generateReviewerInvitationToken(reviewArticle.getUser(), reviewArticle);
        context.setVariable("firstActionLink", String.format("%s/api/review-articles/accept?token=%s", environment.getProperty("SERVER_HOSTNAME"), token));
        context.setVariable("firstActionName", "Đồng ý");
        context.setVariable("secondActionLink", String.format("%s/api/review-articles/reject?token=%s", environment.getProperty("SERVER_HOSTNAME"), token));
        context.setVariable("secondActionName", "Từ chối");
        MailRequest mailRequest = new MailRequest(reviewArticle.getUser().getEmail(), subject, body, context);
        sendEmail(mailRequest);
    }
}