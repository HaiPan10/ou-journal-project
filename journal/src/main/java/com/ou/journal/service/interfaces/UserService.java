package com.ou.journal.service.interfaces;

import com.ou.journal.pojo.User;

public interface UserService {
    User create(User user, String roleName) throws Exception;
    User createAuthorUser(User user) throws Exception;
    User retrieve(Long id) throws Exception;
}
