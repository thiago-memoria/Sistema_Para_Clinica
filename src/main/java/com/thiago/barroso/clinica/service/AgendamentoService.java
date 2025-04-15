package com.thiago.barroso.clinica.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thiago.barroso.clinica.domain.Agendamento;
import com.thiago.barroso.clinica.domain.Horario;
import com.thiago.barroso.clinica.repository.AgendamentoRepository;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class AgendamentoService {
	
	@Autowired
	private AgendamentoRepository repository;
	
	@Transactional(readOnly = true)
	public List<Horario> buscarHorariosNaoAgendadosPorMedicoIdEData(Long id, LocalDate data){
		return repository.findByMedicoIdAndDataNotHorarioAgendado(id, data);
	}
	
	
	@Transactional(readOnly = false)
	public void salvar(Agendamento agendamento) {
		repository.save(agendamento);
	}

	@Transactional(readOnly = true)
	public Object buscarHistoricoPorPacienteEmail(String username, HttpServletRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Transactional(readOnly = true)
	public Object buscarHistoricoPorMedicoEmail(String username, HttpServletRequest request) {
		// TODO Auto-generated method stub
		return null;
	}
	
	 
}
