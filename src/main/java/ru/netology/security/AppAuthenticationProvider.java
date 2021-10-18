package ru.netology.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;

import ru.netology.entity.Session;
import ru.netology.service.AuthenticationService;
import ru.netology.service.exception.SessionNotFoundException;

@Component
public class AppAuthenticationProvider implements AuthenticationProvider {

    public static final String ROLE_AUTHORIZED = "ROLE_AUTHORIZED";

    @Autowired
    private AuthenticationService authenticationService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        PreAuthenticatedAuthenticationToken token = (PreAuthenticatedAuthenticationToken) authentication;

        try {
            Session session = authenticationService.getSession((String)token.getPrincipal(), (String)token.getCredentials());

            return new PreAuthenticatedAuthenticationToken(session, null, AuthorityUtils.createAuthorityList(ROLE_AUTHORIZED));
        } catch (SessionNotFoundException e) {
            throw new BadCredentialsException("Bad token");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return PreAuthenticatedAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
