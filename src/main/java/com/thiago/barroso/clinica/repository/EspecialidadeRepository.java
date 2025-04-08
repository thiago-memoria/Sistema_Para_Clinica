package com.thiago.barroso.clinica.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.thiago.barroso.clinica.domain.Especialidade;

public interface EspecialidadeRepository extends JpaRepository<Especialidade, Long>{

	@Query("select e from Especialidade e where e.titulo like :search%")
	Page<Especialidade> findAllByTitulo(String search, Pageable pageable);
	
	@Query("select e.titulo from Especialidade e where e.titulo like :%titulo")
	List<String> findEspecialidadesByTermo(String termo);
}
