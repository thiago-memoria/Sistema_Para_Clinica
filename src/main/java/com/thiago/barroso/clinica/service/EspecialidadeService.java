package com.thiago.barroso.clinica.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thiago.barroso.clinica.datatables.Datatables;
import com.thiago.barroso.clinica.datatables.DatatablesColunas;
import com.thiago.barroso.clinica.domain.Especialidade;
import com.thiago.barroso.clinica.repository.EspecialidadeRepository;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class EspecialidadeService {

	@Autowired
	private EspecialidadeRepository repository;
	@Autowired
	private Datatables datatables;
	
	@Transactional(readOnly = false)
	public void salvar(Especialidade especialidade) {
		repository.save(especialidade);
	}
	
	@Transactional(readOnly = true)
	public Map<String, Object> buscarEspecialidades(HttpServletRequest request){
		datatables.setRequest(request);
		datatables.setColunas(DatatablesColunas.ESPECIALIDADES);
		Page<?> page = datatables.getSearch().isEmpty()
				? repository.findAll(datatables.getPageable())
				: repository.findAllByTitulo(datatables.getSearch(), datatables.getPageable());
		return datatables.getResponse(page);
	}
	
	@Transactional(readOnly = true)
	public Especialidade buscarPorId(Long id) {
		return repository.findById(id).get();
	}
	
	@Transactional(readOnly = false)
	public void remover(Long id) {
		repository.deleteById(id);
	}
	
}
