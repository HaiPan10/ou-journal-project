package com.ou.journal.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.ou.journal.pojo.Account;
import com.ou.journal.pojo.User;

/**
 *
 * @author HaiPhan
 */
@Component
public class PassValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return Account.class.isAssignableFrom(clazz) || User.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        if (target instanceof Account) {
            Account account = (Account) target;
            if (!account.getPassword().equals(account.getConfirmPassword())) {
                errors.rejectValue("confirmPassword",
                        "{account.password.notMatchMsg}",
                        "Mật khẩu không khớp!");
            }
        } else if (target instanceof User) {
            User user = (User) target;
            if (!user.getAccount().getPassword().equals(user.getAccount().getConfirmPassword())) {
                errors.rejectValue("account.confirmPassword",
                        "{account.password.notMatchMsg}",
                        "Mật khẩu không khớp!");
            }
        }
    }

}
