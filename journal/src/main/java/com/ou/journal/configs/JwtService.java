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
import com.ou.journal.pojo.Account;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class JwtService {
    private static final String SECRECT = "ajfipupieuqwpieuasipdhfajlbfljh3y012637018274hfajlsdadfqweasdadfa3123123123123";
    private static final long HOUR = 365;
    private static final long MINUTE = 60;
    private static final long SECOND = 60;
    private static final long MILISECOND = 1000;
    private static final long EXPIRE_DURATION = HOUR * MINUTE * SECOND * MILISECOND;
    private static final byte[] BYTES = SECRECT.getBytes();

    public String generateAccessToken(Account account){
        String token = null;
        if(account != null){
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

    private JWTClaimsSet getClaimsSet(String token){
        JWTClaimsSet claimsSet = null;
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWSVerifier verifier = new MACVerifier(BYTES);
            if(signedJWT.verify(verifier)){
                claimsSet = signedJWT.getJWTClaimsSet();
            }
        } catch (JOSEException | ParseException e) {
            System.out.println("[ERROR] - " + e.getMessage());
        }
        return claimsSet;
    }

    private Date getExpirationDate(String token){
        JWTClaimsSet claimsSet = getClaimsSet(token);
        Date expirationDate = claimsSet.getExpirationTime();
        return expirationDate;
    }

    public boolean isValidAccessToken(String token){
        if(token == null || token.trim().length() == 0){
            return false;
        }

        Date expirationDate = getExpirationDate(token);
        String userName = getUserNameFromToken(token);
        Long id = getIdFromToken(token);
        // System.out.println("[DEBUG] - " + userName);
        // System.out.println("[DEBUG] - " + id);
        // System.out.println("[DEBUG] - " + expirationDate);
        // System.out.println("[DEBUG] - " + !(userName == null || userName.isEmpty() || id == null || expirationDate.before(new Date())));
        return !(userName == null || userName.isEmpty() || id == null || expirationDate.before(new Date()));
    }

    /**
     * 
     * @param token
     * @return a string with format [id,userName]
     */
    public String getUserNameFromToken(String token){
        JWTClaimsSet claimsSet = getClaimsSet(token);
        String value = null;
        try {
            value = claimsSet.getStringClaim("userName");
        } catch (ParseException e) {
            System.out.println("[ERROR] - " + e.getMessage());
        }
        return value;
    }

    public Long getIdFromToken(String token){
        JWTClaimsSet claimsSet = getClaimsSet(token);
        Long value = null;
        try {
            value = claimsSet.getLongClaim("id");
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
        String token = header.split(" ")[1].trim();
        return token;
    }

    public String getAccountId(HttpServletRequest request){
        String header = getAuthorization(request);
        String token = getAccessToken(header);
        Long id = getIdFromToken(token);
        return String.valueOf(id);
    }
}
