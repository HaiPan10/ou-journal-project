package com.ou.journal.service.interfaces;

import com.ou.journal.pojo.Role;

public interface RoleService {
    Role retrieve(String roleName) throws Exception;
}
