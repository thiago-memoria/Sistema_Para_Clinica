package com.thiago.barroso.clinica.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.thiago.barroso.clinica.domain.Medico;

public interface MedicoRepository extends JpaRepository<Medico, Long>{
	
	@Query("select m form Medico m where m.usuario.id = :id")
	Optional<Medico> findByUsuarioId(Long id);
	
}
