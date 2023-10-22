package com.ou.journal.service.interfaces;

import java.util.List;

import com.ou.journal.pojo.User;

public interface UserService {
    User create(User user, String roleName) throws Exception;
    User create(User user);
    User createAuthorUser(User user) throws Exception;
    User retrieve(Long id) throws Exception;
    User findByEmail(String email) throws Exception;
    List<Object[]> listUser();
}
