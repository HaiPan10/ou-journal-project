package com.ou.journal.components;

import java.io.Serializable;

import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;


public class PostPermission implements PermissionEvaluator {
    // @Autowired
    // private PostService postService;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetObject, Object permission) {
        return true;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetPost, String targetType,
            Object permission) {
        // if ("Post".equals(targetType) && "edit".equals(permission)) {
        //     Post post = null;
        //     if (targetPost instanceof Post) {
        //         post = (Post) targetPost;
        //     } else {
        //         return false;
        //     }

        //     String email = authentication.getPrincipal().toString();
        //     try {
        //         persistPost = postService.retrieve(post.getId());
        //     } catch (Exception e) {
        //         return false;
        //     }
        //     if (targetPost.getUserId().getAccount().getEmail().equals(email)) {

        //     }
            
        // }
        return true;
    }
}
