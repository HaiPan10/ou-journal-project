package com.ou.journal.api;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ou.journal.configs.JwtService;
import com.ou.journal.enums.SecrectType;
import com.ou.journal.pojo.AuthenticationUser;
import com.ou.journal.pojo.Role;
import com.ou.journal.pojo.User;
import com.ou.journal.service.interfaces.AccountService;
import com.ou.journal.service.interfaces.UserService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("api/accounts")
public class ApiAccountController {
    // @Autowired
    // private JwtService jwtService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private UserService userService;

    @Autowired
    private Environment environment;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private SecurityContextRepository securityContextRepository;

    @PostMapping("change-role")
    public ResponseEntity<?> changeRole(@RequestBody Role roleName,
            @AuthenticationPrincipal AuthenticationUser currentUser, HttpServletResponse response) {
        try {
            Long id = currentUser.getId();
            User user = userService.retrieve(id);
            accountService.changeRole(roleName, user);
            Cookie cookie = new Cookie("ROLE", roleName.getRoleName());
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            cookie.setMaxAge(Integer.parseInt(environment.getProperty("COOKIE_MAX_AGE")));
            cookie.setPath("/");
            response.addCookie(cookie);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("create")
    public ResponseEntity<?> create(@RequestParam(required = false, name = "token") String token,
            HttpServletRequest httpServletRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(
                String.format("%s/register/account?token=%s", environment.getProperty("SERVER_HOSTNAME"), token)));
        return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
    }

    /**
     * This is the login API by using the token as the request paramerter
     * calling the API won't invoke the remember me service. If the {@link SecurityContext}
     * have already stored the Authentication then the API will return the 301 MOVE PERMANENTLY
     * @param token A JWT token string
     * @param httpServletRequest HttpServletRequest
     * @param response HttpServletResponse
     * @param user The currently authenticated user
     * @return ResponseEntity with status 404 NOT FOUND if the token is invalid
     * and status 301 MOVE PERMANENTLY if the token is valid and authenticate
     * user information within the token successfully
     * @author Hai Phan
     */
    @GetMapping("login")
    public ResponseEntity<?> login(@RequestParam(required = false, name = "token") String token,
            HttpServletRequest httpServletRequest, HttpServletResponse response,
            @AuthenticationPrincipal AuthenticationUser user) {
        if (!jwtService.isValidAccessToken(token, SecrectType.EMAIL)) {
            System.out.println("[DEBUG] - TOKEN IS INVALID");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        try {
            if (user == null) {
                SecurityContext securityContext = accountService.login(token);
                securityContextRepository.saveContext(securityContext, httpServletRequest, response);
            }
        } catch (Exception e) {
            System.out.println("[ERROR] - " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        String targetEndpoint = jwtService.getTargetEndpointFromToken(token, SecrectType.EMAIL);
        if (targetEndpoint == null || targetEndpoint.trim().isEmpty()) {
            //default endpoint
            targetEndpoint = environment.getProperty("DEFAULT_ENDPOINT");
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(
                String.format("%s/%s", environment.getProperty("SERVER_HOSTNAME"), targetEndpoint)));
        ResponseEntity<?> responseEntity = new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
        return responseEntity;
    }
}
