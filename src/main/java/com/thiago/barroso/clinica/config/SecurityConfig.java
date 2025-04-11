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
						
						// Acessos privados admin
						.requestMatchers("/u/editar/senha", "/u/confirmar/senha").hasAuthority(MEDICO)
						.requestMatchers("/u/**").hasAuthority(ADMIN)
						
						// Acessos privados medicos
						.requestMatchers("/medicos/dados", "/medicos/salvar", "medicos/editar").hasAnyAuthority(MEDICO, ADMIN)
						.requestMatchers("/medicos/**").hasAuthority(MEDICO) 
						
						// Acessos privados pacientes
						.requestMatchers("/pacientes/**").hasAnyAuthority(PACIENTE)
						
						//Acessos privados especialidades
						.requestMatchers("/especialidades/datatables/server/medico/*").hasAnyAuthority(MEDICO, ADMIN)
						.requestMatchers("/especialidades/titulo").hasAnyAuthority(MEDICO, ADMIN)
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
