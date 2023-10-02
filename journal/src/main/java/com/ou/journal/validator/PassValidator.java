package com.ou.journal.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.ou.journal.pojo.Account;

/**
 *
 * @author HaiPhan
 */
@Component
public class PassValidator implements Validator{

    @Override
    public boolean supports(Class<?> clazz) {
        return Account.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        if(target instanceof Account){
            Account account = (Account) target;
            if(!account.getPassword().equals(account.getConfirmPassword())){
                errors.reject("{account.password.notMatchMsg}", "Mật khẩu không khớp!");
            }
        }
    }
    
}
