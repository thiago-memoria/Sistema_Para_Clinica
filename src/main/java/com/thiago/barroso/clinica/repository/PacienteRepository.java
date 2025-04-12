package com.thiago.barroso.clinica.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.thiago.barroso.clinica.domain.Paciente;

public interface PacienteRepository extends JpaRepository<Paciente, Long>{

	@Query("select p from paciente p where p.usuario.email like :email")
	Optional<Paciente> findByUsuarioEmail(String email);
	
}
