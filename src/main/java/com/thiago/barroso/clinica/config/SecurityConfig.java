package com.thiago.barroso.clinica.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import com.thiago.barroso.clinica.domain.PerfilTipo;
import com.thiago.barroso.clinica.service.UsuarioService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	private static final String ADMIN = PerfilTipo.ADMIN.getDesc();
	private static final String MEDICO = PerfilTipo.MEDICO.getDesc();
	private static final String PACIENTE = PerfilTipo.PACIENTE.getDesc();
	
	@Autowired
	private UsuarioService service;
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		return http
				.authorizeHttpRequests(auth -> auth
						.requestMatchers(HttpMethod.GET, "/", "/home").permitAll()
						.requestMatchers("/webjars/**", "/css/**", "/image/**", "/js/**").permitAll()
						.requestMatchers("/u/novo/cadastro", "/u/cadastro/realizado", "/u/cadastro/paciente/salvar").permitAll()
						.requestMatchers("/u/confirmacao/cadastro").permitAll()
						.requestMatchers("/u/p/**").permitAll()
						
						// Acessos privados admin
						.requestMatchers("/u/editar/senha", "/u/confirmar/senha").hasAnyAuthority(PACIENTE, MEDICO)
						.requestMatchers("/u/**").hasAuthority(ADMIN)
						
						// Acessos privados medicos
						.requestMatchers("/medicos/dados", "/medicos/salvar", "medicos/editar").hasAnyAuthority(MEDICO, ADMIN)
						.requestMatchers("/medicos/**").hasAuthority(MEDICO) 
						
						// Acessos privados pacientes
						.requestMatchers("/pacientes/**").hasAnyAuthority(PACIENTE)
						
						//Acessos privados especialidades
						.requestMatchers("/especialidades/datatables/server/medico/*").hasAnyAuthority(MEDICO, ADMIN)
						.requestMatchers("/especialidades/titulo").hasAnyAuthority(MEDICO, ADMIN, PACIENTE)
						.requestMatchers("/especialidades/**").hasAnyAuthority(ADMIN)

						
						.anyRequest().authenticated()
					
				)
				.formLogin(form -> form
		                .loginPage("/login")
		                .defaultSuccessUrl("/", true)
		                .failureUrl("/login-error")
		                .permitAll()
		            )
		        .logout(logout -> logout
		        		.logoutSuccessUrl("/")
		        		.deleteCookies("JSESSIONDID")
		            )
		        .exceptionHandling(exception -> exception
		        		.accessDeniedPage("/acesso-negado")
                    )
		        .rememberMe(remember -> remember
		                .key("keyRemember")
		                .tokenValiditySeconds(1209600)
		            )
		        .sessionManagement(session -> session
		        		.maximumSessions(1)
		    			.maxSessionsPreventsLogin(true)
		    			.sessionRegistry(sessionRegistry())
		        		)
		        .build();
	}
	
	@Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = 
                http.getSharedObject(AuthenticationManagerBuilder.class);

        authenticationManagerBuilder
            .userDetailsService(service)
            .passwordEncoder(passwordEncoder());

        return authenticationManagerBuilder.build();
    }

	    @Bean
	    public PasswordEncoder passwordEncoder() {
	        return new BCryptPasswordEncoder();
	    }
	    
	    @Bean
	    public SessionRegistry sessionRegistry() {
	    	return new SessionRegistryImpl();
	    }
	    
	    @Bean
	    public ServletListenerRegistrationBean<?> servletListenerRegistrationBean(){
	    	return new ServletListenerRegistrationBean<>( new HttpSessionEventPublisher());
	    }
}
