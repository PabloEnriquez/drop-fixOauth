package com.itesm.oauth;

import org.eclipse.jetty.server.ConnectionFactory;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.ForwardedRequestCustomizer;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.security.Principal;

@SpringBootApplication
@RestController
@SessionAttributes("authorizationRequest")
@EnableResourceServer
public class Application extends WebMvcConfigurerAdapter{

	private static final Logger logger = LoggerFactory.getLogger(Application.class);

	static final int WEB_ORDER = -10;

	@RequestMapping("/usuario")
	@ResponseBody
	public Principal user(Principal user) {
		return user;
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/login").setViewName("login");
	}

	@Bean
	public EmbeddedServletContainerCustomizer containerCustomizer() {
		return new EmbeddedServletContainerCustomizer() {
			@Override
			public void customize(ConfigurableEmbeddedServletContainer container) {

				if (container instanceof JettyEmbeddedServletContainerFactory) {
					logger.debug("container is jetty");
					JettyEmbeddedServletContainerFactory containerFactory = (JettyEmbeddedServletContainerFactory)container;
					containerFactory.addServerCustomizers(server -> {
						for (Connector connector : server.getConnectors()) {
							ConnectionFactory connectionFactory = connector.getDefaultConnectionFactory();
							if (connectionFactory instanceof HttpConnectionFactory) {
								HttpConnectionFactory defaultConnectionFactory = (HttpConnectionFactory)connectionFactory;
								HttpConfiguration httpConfiguration = defaultConnectionFactory.getHttpConfiguration();
								httpConfiguration.addCustomizer(new ForwardedRequestCustomizer());
							}
						}
					});
				} else {
					logger.debug("container not jetty");
				}
			}
		};
	}

	@Bean
	public DefaultRedirectStrategy defaultRedirectStrategy() {
		DefaultRedirectStrategy drs = new DefaultRedirectStrategy();
		drs.setContextRelative(true);
		return drs;
	}


	/**
	 * Login config.
	 */
	@Configuration
	@Order(WEB_ORDER)
	protected static class LoginConfig extends WebSecurityConfigurerAdapter {

		@Autowired
		private AuthenticationManager authenticationManager;


		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http
					.csrf().disable()
					.formLogin().loginPage("/login").permitAll()
					.and()
					.requestMatchers()
					.antMatchers("/login", "/oauth/authorize")
					.and()
					.authorizeRequests().anyRequest().authenticated();
		}

		@Override
		protected void configure(AuthenticationManagerBuilder auth) throws Exception {
			auth.parentAuthenticationManager(authenticationManager);
		}
	}

	@Bean
	public FilterRegistrationBean simpleCorsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true);
		config.addAllowedOrigin("*");
		config.addAllowedHeader("*");
		config.addAllowedMethod("*");
		source.registerCorsConfiguration("/**", config);
		FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
		bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
		return bean;
	}
}
