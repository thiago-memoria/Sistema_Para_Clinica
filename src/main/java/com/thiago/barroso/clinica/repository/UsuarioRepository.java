package com.thiago.barroso.clinica.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.thiago.barroso.clinica.domain.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long>{
	
	@Query("select u from Usuario u where u.email like :email")
	Usuario findByEmail(@Param("email")String email);
	
	@Query("select u from Usuario u "
			+ "join u.perfis p "
			+"where u.email like :search% OR p.desc like :search%")
	Page<Usuario> findByEmailOrPerfil(String search, Pageable pageable);
	
	@Query("select u from Usuario u "
			+ "join u.perfis p "
			+"where u.id = :usuarioId AND p.id IN :perfisId")
	Usuario findByIdAndPerfis(Long usuarioId, Long[] perfisId);
	
	@Query("select u from Usuario u where u.email like :email AND u.ativo = true")
	Optional<Usuario> findByEmailAndAtivo(String email);
	
}
