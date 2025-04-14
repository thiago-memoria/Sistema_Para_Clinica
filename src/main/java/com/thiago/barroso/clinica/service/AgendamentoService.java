package com.thiago.barroso.clinica.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thiago.barroso.clinica.domain.Horario;
import com.thiago.barroso.clinica.repository.AgendamentoRepository;

@Service
public class AgendamentoService {
	
	@Autowired
	private AgendamentoRepository repository;
	
	@Transactional
	public List<Horario> buscarHorariosNaoAgendadosPorMedicoIdEData(Long id, LocalDate data){
		return repository.findByMedicoIdAndDataNotHorarioAgendado(id, data);
	}
	
}
