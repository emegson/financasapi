package br.com.controle.financeiro.configuration.security.auth.firebase;

import br.com.controle.financeiro.service.impl.UserServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class FirebaseAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    @Qualifier(value = UserServiceImpl.NAME)
    private UserDetailsService userService;

    public boolean supports(Class<?> authentication) {
        return (FirebaseAuthenticationToken.class.isAssignableFrom(authentication));
    }

    public Authentication authenticate(Authentication authentication) {
        if (!supports(authentication.getClass())) {
            return null;
        }

        FirebaseAuthenticationToken authenticationToken = (FirebaseAuthenticationToken) authentication;
        UserDetails details = userService.loadUserByUsername(authenticationToken.getName());
        if (details == null) {
            throw new UsernameNotFoundException("not found");
        }

        authenticationToken =
                new FirebaseAuthenticationToken(details, authentication.getCredentials(), details.getAuthorities());

        return authenticationToken;
    }

}
