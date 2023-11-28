package com.ou.journal.components;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.ou.journal.pojo.CustomAuthenticationToken;
import com.ou.journal.service.interfaces.CustomUserDetailsService;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CustomAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider{
    private PasswordEncoder passwordEncoder;

    private CustomUserDetailsService userDetailsService;

	// private UserDetailsPasswordService userDetailsPasswordService;

    public CustomAuthenticationProvider(PasswordEncoder passwordEncoder){
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails,
            UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        if (authentication.getCredentials() == null) {
			this.logger.debug("Failed to authenticate since no credentials provided");
			throw new BadCredentialsException(this.messages
					.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
		}
		String presentedPassword = authentication.getCredentials().toString();
		if (!this.passwordEncoder.matches(presentedPassword, userDetails.getPassword())) {
			this.logger.debug("Failed to authenticate since password does not match stored value");
			throw new BadCredentialsException(this.messages
					.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
		}
    }

    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication)
            throws AuthenticationException {
        // prepareTimingAttackProtection();
		try {
            UserDetails loadedUser = null;
            if(authentication instanceof CustomAuthenticationToken){
                CustomAuthenticationToken token = (CustomAuthenticationToken) authentication;
                loadedUser = this.getUserDetailsService().loadUserByUsernameAndRoleName(username, token.getRoleName());
            } else {
                loadedUser = this.getUserDetailsService().loadUserByUsername(username);
            }
			if (loadedUser == null) {
				throw new InternalAuthenticationServiceException(
						"UserDetailsService returned null, which is an interface contract violation");
			}
			return loadedUser;
		}
		catch (UsernameNotFoundException ex) {
			// mitigateAgainstTimingAttack(authentication);
			throw ex;
		}
		catch (InternalAuthenticationServiceException ex) {
			throw ex;
		}
		catch (Exception ex) {
			throw new InternalAuthenticationServiceException(ex.getMessage(), ex);
		}
    }
    
}
