package com.thiago.barroso.clinica.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.thiago.barroso.clinica.domain.Agendamento;

@Controller
@RequestMapping("agendamentos")
public class AgendamentoController {
	
	@GetMapping("{/agendar}")
	public String agendarConsulta(Agendamento agendamento) {
		return"agendamento/cadastro";
	}
}
