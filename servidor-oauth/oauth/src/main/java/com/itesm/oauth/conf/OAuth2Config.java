package com.itesm.oauth.conf;

import com.itesm.oauth.service.AuthProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;

/**
 * Configuracion para OAuth2.
 *
 * @author mklfarha
 *
 */
@Configuration
@EnableAuthorizationServer
public class OAuth2Config extends AuthorizationServerConfigurerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2Config.class);
    
    @Autowired
    private AuthProvider authenticationProvider;
   
    @Autowired
    private AuthenticationManager authenticationManager;

    @Value("${itesm.oauth.tokenTimeout:-1}")
    private int expiration;



    @Override
    public void configure(AuthorizationServerEndpointsConfigurer configurer) throws Exception {
        configurer.authenticationManager(authenticationManager)
                .approvalStoreDisabled();
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient("demo")
                .secret("secret")
                .accessTokenValiditySeconds(expiration)
                .scopes("read", "write")
                .authorizedGrantTypes("authorization_code", "password", "refresh_token", "client_credentials", "implicit")
                .resourceIds("service");
    }
    
    @Autowired
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        logger.debug("configurando provider");

        auth.authenticationProvider(authenticationProvider);
    }

}
