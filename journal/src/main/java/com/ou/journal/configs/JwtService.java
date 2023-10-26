package com.ou.journal.configs;

import java.text.ParseException;
import java.util.Date;

import org.springframework.stereotype.Service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.ou.journal.enums.SecrectType;
import com.ou.journal.pojo.Account;
import com.ou.journal.pojo.ReviewArticle;
import com.ou.journal.pojo.User;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class JwtService {
    private static final String SECRECT = "ajfipupieuqwpieuasipdhfajlbfljh3y012637018274hfajlsdadfqweasdadfa3123123123123";
    private static final String MAIL_SECRECT = "asdfkladsjfiqwuepieu1238701{Dung16052005}712047kasdjkfja;dksjf512340870";
    private static final String REGISTER_SECRECT = "ASKFJASDIFUPAWEIakdfjpiqewur187328017afjk;adjfka?<?<?qwe12312421qrf090;";
    private static final long HOUR = 24;
    private static final long MINUTE = 60;
    private static final long SECOND = 60;
    private static final long MILISECOND = 1000;
    private static final long EXPIRE_DURATION = HOUR * MINUTE * SECOND * MILISECOND;
    private static final byte[] BYTES = SECRECT.getBytes();

    public String generateAccessToken(Account account) {
        String token = null;
        if (account != null) {
            try {
                JWSSigner signer = new MACSigner(BYTES);
                JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder();
                builder.claim("userName", account.getUserName());
                builder.claim("id", account.getId());
                builder.issueTime(new Date(System.currentTimeMillis()));
                builder.expirationTime(new Date(System.currentTimeMillis() + EXPIRE_DURATION));

                JWTClaimsSet claimsSet = builder.build();
                SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
                signedJWT.sign(signer);

                token = signedJWT.serialize();

            } catch (JOSEException e) {
                System.out.println("[ERROR] - " + e.getMessage());
            }
        }
        return token;
    }

    public String generateReviewerInvitationToken(User user, ReviewArticle reviewArticle) {
        String token = null;
        if (user != null) {
            try {
                JWSSigner signer = new MACSigner(MAIL_SECRECT.getBytes());
                JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder();
                builder.claim("id", user.getId());
                builder.claim("email", user.getEmail());
                builder.claim("reviewArticleId", reviewArticle.getId());
                builder.issueTime(new Date(System.currentTimeMillis()));
                builder.expirationTime(new Date(System.currentTimeMillis() + EXPIRE_DURATION));

                JWTClaimsSet claimsSet = builder.build();
                SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
                signedJWT.sign(signer);

                token = signedJWT.serialize();

            } catch (JOSEException e) {
                System.out.println("[ERROR] - " + e.getMessage());
            }
        }
        return token;
    }

    public String generateToken(String email) {
        String token = null;
        try {
            
            JWSSigner signer = new MACSigner(REGISTER_SECRECT.getBytes());
            JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder();
            builder.claim("email", email);
            // builder.claim("verificationCode", account.getVerificationCode());
            builder.issueTime(new Date(System.currentTimeMillis()));
            builder.expirationTime(new Date(System.currentTimeMillis() + EXPIRE_DURATION));

            JWTClaimsSet claimsSet = builder.build();
            SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
            signedJWT.sign(signer);

            token = signedJWT.serialize();

        } catch (JOSEException e) {
            System.out.println("[ERROR] - " + e.getMessage());
        } catch (NullPointerException e){
            System.out.println("[ERROR] - " + e.getMessage());
        }
        return token;
    }

    public String generateToken(User user){
        String token = null;
        try {
            
            JWSSigner signer = new MACSigner(MAIL_SECRECT.getBytes());
            JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder();
            builder.claim("email", user.getEmail());
            builder.claim("id", user.getId());
            // builder.claim("verificationCode", account.getVerificationCode());
            builder.issueTime(new Date(System.currentTimeMillis()));
            builder.expirationTime(new Date(System.currentTimeMillis() + EXPIRE_DURATION));

            JWTClaimsSet claimsSet = builder.build();
            SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
            signedJWT.sign(signer);

            token = signedJWT.serialize();

        } catch (JOSEException e) {
            System.out.println("[ERROR] - " + e.getMessage());
        } catch (NullPointerException e){
            System.out.println("[ERROR] - " + e.getMessage());
        }
        return token;
    }

    private JWTClaimsSet getClaimsSet(String token, SecrectType secrectType) {
        JWTClaimsSet claimsSet = null;
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWSVerifier verifier = null;
            if (secrectType.equals(SecrectType.DEFAULT)) {
                verifier = new MACVerifier(BYTES);
            } else if(secrectType.equals(SecrectType.EMAIL)){
                verifier = new MACVerifier(MAIL_SECRECT);
            } else if(secrectType.equals(SecrectType.REGISTER)){
                verifier = new MACVerifier(REGISTER_SECRECT);
            }

            if (signedJWT.verify(verifier)) {
                claimsSet = signedJWT.getJWTClaimsSet();
            }
        } catch (JOSEException | ParseException e) {
            System.out.println("[ERROR] - " + e.getMessage());
        }
        return claimsSet;
    }

    private Date getExpirationDate(String token, SecrectType secrectType) {
        JWTClaimsSet claimsSet = getClaimsSet(token, secrectType);
        Date expirationDate = claimsSet.getExpirationTime();
        return expirationDate;
    }

    public boolean isValidAccessToken(String token) {
        if (token == null || token.trim().length() == 0) {
            return false;
        }

        Date expirationDate = getExpirationDate(token, SecrectType.DEFAULT);
        String userName = getUserNameFromToken(token, SecrectType.DEFAULT);
        Long id = getIdFromToken(token, SecrectType.DEFAULT);
        // System.out.println("[DEBUG] - " + userName);
        // System.out.println("[DEBUG] - " + id);
        // System.out.println("[DEBUG] - " + expirationDate);
        // System.out.println("[DEBUG] - " + !(userName == null || userName.isEmpty() ||
        // id == null || expirationDate.before(new Date())));
        return !(userName == null || userName.isEmpty() || id == null || expirationDate.before(new Date()));
    }

    public boolean isValidAccessToken(String token, SecrectType secrectType){
        if(SecrectType.DEFAULT.equals(secrectType)){
            return isValidAccessToken(token);
        }

        if (token == null || token.trim().length() == 0) {
            return false;
        }

        Date expirationDate = getExpirationDate(token, secrectType);
        Long id = getIdFromToken(token, secrectType);
        String email = getEmailFromToken(token, secrectType);
        return !(email == null || email.isEmpty() || id == null || expirationDate.before(new Date()));
    }

    /**
     * 
     * @param token
     * @return a string with format [id,userName]
     */
    public String getUserNameFromToken(String token, SecrectType secrectType) {
        JWTClaimsSet claimsSet = getClaimsSet(token, secrectType);
        String value = null;
        try {
            value = claimsSet.getStringClaim("userName");
        } catch (ParseException e) {
            System.out.println("[ERROR] - " + e.getMessage());
        }
        return value;
    }

    public Long getIdFromToken(String token, SecrectType secrectType) {
        JWTClaimsSet claimsSet = getClaimsSet(token, secrectType);
        Long value = null;
        try {
            value = claimsSet.getLongClaim("id");
        } catch (ParseException e) {
            System.out.println("[ERROR] - " + e.getMessage());
        }
        return value;
    }

    public Long getUserIdFromToken(String token, SecrectType secrectType) {
        JWTClaimsSet claimsSet = getClaimsSet(token, secrectType);
        Long value = null;
        try {
            value = claimsSet.getLongClaim("id");
        } catch (ParseException e) {
            System.out.println("[ERROR] - " + e.getMessage());
        }
        return value;
    }

    public String getEmailFromToken(String token, SecrectType secrectType) {
        JWTClaimsSet claimsSet = getClaimsSet(token, secrectType);
        String value = null;
        try {
            value = claimsSet.getStringClaim("email");
        } catch (ParseException e) {
            System.out.println("[ERROR] - " + e.getMessage());
        }
        return value;
    }

    public Long getReviewArticleIdFromToken(String token, SecrectType secrectType) {
        JWTClaimsSet claimsSet = getClaimsSet(token, secrectType);
        Long value = null;
        try {
            value = claimsSet.getLongClaim("reviewArticleId");
        } catch (ParseException e) {
            System.out.println("[ERROR] - " + e.getMessage());
        }
        return value;
    }

    public String getAuthorization(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("Authorization")) {
                        header = "Bearer " + cookie.getValue();
                        break;
                    }
                }
            }
        }
        return header;
    }

    public String getAccessToken(String header) {
        if (header == null) {
            return null;
        }
        String token = header.split(" ")[1].trim();
        return token;
    }

    public Long getAccountId(HttpServletRequest request, SecrectType secrectType) {
        String header = getAuthorization(request);
        String token = getAccessToken(header);
        Long id = getIdFromToken(token, secrectType);
        return id;
    }

    public String getVerificationCodeFromToken(String token, SecrectType secrectType) {
        JWTClaimsSet claimsSet = getClaimsSet(token, secrectType);
        String value = null;
        try {
            value = claimsSet.getStringClaim("verificationCode");
        } catch (ParseException e) {
            System.out.println("[ERROR] - " + e.getMessage());
        }
        return value;
    }
}
