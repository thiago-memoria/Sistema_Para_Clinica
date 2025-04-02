package com.thiago.barroso.clinica.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.thiago.barroso.clinica.service.UsuarioService;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Autowired
	private UsuarioService service;
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		return http
				.authorizeHttpRequests(auth -> auth
						.requestMatchers(HttpMethod.GET, "/", "/home").permitAll()
						.requestMatchers("/webjars/**", "/css/**", "/image/**", "/js/**").permitAll()
						
						// Acessos privados admin
						.requestMatchers("/u/**").hasAuthority("ADMIN")
						
						// Acessos privados medicos
						.requestMatchers("/medicos/**").hasAuthority("MEDICOS")
						
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
		            )
		        .exceptionHandling(exception -> exception
		        		.accessDeniedPage("/acesso-negado")
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

}
