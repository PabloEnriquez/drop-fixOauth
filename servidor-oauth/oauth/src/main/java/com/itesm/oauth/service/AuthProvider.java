package com.itesm.oauth.service;


import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.ArrayList;
import java.util.Map;


/**
 *
 * @author mklfarha
 * AuthProvider for Oauth2
 */

@Service
public class AuthProvider implements AuthenticationProvider {

    @Autowired
    private JdbcTemplate template;

    static final Logger logger = LoggerFactory.getLogger(AuthProvider.class);


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        final String username = (String)authentication.getPrincipal();
        final String password = (String)authentication.getCredentials();

        if (isValidEmailAddress(username)) {

            Map<String,Object> user = template.queryForMap("Select * from usuario where email = ? limit 1", username);

            if (user == null) {
                throw new BadCredentialsException("Persona no encontrada con el nombre usuario/correo: " + username);
            }

            if (!user.get("contrasena").equals(DigestUtils.sha1Hex(password))) {
                throw new BadCredentialsException("Identificación Incorrecta");
            }
            user.put("contrasena",null);
            return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>() );
        } else {
            throw new BadCredentialsException("Correo con formato incorrecto: " + username);
        }


    }

    public static boolean isValidEmailAddress(String email) {
        boolean result = true;
        try {
           InternetAddress emailAddr = new InternetAddress(email);
           emailAddr.validate();
        } catch (AddressException ex) {
           result = false;
        }
        return result;
     }

    @Override
    public boolean supports(Class<?> type) {
        logger.debug("supports class: {}", type);
        return type.equals(UsernamePasswordAuthenticationToken.class);
    }

}
