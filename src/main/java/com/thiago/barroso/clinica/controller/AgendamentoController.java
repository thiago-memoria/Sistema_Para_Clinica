package com.thiago.barroso.clinica.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.thiago.barroso.clinica.domain.Agendamento;
import com.thiago.barroso.clinica.domain.Especialidade;
import com.thiago.barroso.clinica.domain.Paciente;
import com.thiago.barroso.clinica.domain.PerfilTipo;
import com.thiago.barroso.clinica.service.AgendamentoService;
import com.thiago.barroso.clinica.service.EspecialidadeService;
import com.thiago.barroso.clinica.service.PacienteService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("agendamentos")
public class AgendamentoController {
	
	@Autowired
	private AgendamentoService service;
	
	@Autowired
	private PacienteService pacienteService;
	
	@Autowired
	private EspecialidadeService especialidadeService;
	
	@PreAuthorize("hasAnyAuthorizy('PACIENTE', 'MEDICO')")
	@GetMapping("/agendar")
	public String agendarConsulta(Agendamento agendamento) {
		return"agendamento/cadastro";
	}
	
	@PreAuthorize("hasAnyAuthorizy('PACIENTE', 'MEDICO')")
	@GetMapping("/horario/medico/{id}/data/{data}")
	public ResponseEntity<?> getHorarios(@PathVariable("id") Long id,
										 @PathVariable("data") @DateTimeFormat(iso = ISO.DATE) LocalDate data){
		return  ResponseEntity.ok(service.buscarHorariosNaoAgendadosPorMedicoIdEData(id, data));
	}
	@PreAuthorize("hasAuthorizy('PACIENTE')")
	@PostMapping("/salvar")
	public String salvar(Agendamento agendamento, RedirectAttributes attr, @AuthenticationPrincipal User user) {
		Paciente paciente = pacienteService.buscarPorUsuarioEmail(user.getUsername());
		String titulo = agendamento.getEspecialidade().getTitulo();
		Especialidade especialidade = especialidadeService
				.buscarPorTitulos(new String [] {titulo})
				.stream().findFirst().get();
		agendamento.setEspecialidade(especialidade);
		agendamento.setPaciente(paciente);
		service.salvar(agendamento);
		attr.addFlashAttribute("sucesso", "Sua consulta foi agendada com sucesso.");
		return "redirect:/agendamento/agendar";
	}
	
	// abrir pagina de historico de agedndamento do paciente
	@PreAuthorize("hasAnyAuthorizy('PACIENTE', 'MEDICO')")
	@GetMapping({"/historico/paciente", "/historico/consultas"})
	public String historico() {
		return "agendamento/historico/paciente";
	}
	
	// localizar o historico de agendamentos por usuario logado
	@PreAuthorize("hasAnyAuthorizy('PACIENTE', 'MEDICO')")
	@GetMapping("/datatables/server/historico")
	public ResponseEntity<?> historicoAgendamentosPorPaciente(HttpServletRequest request,
			@AuthenticationPrincipal User user){
		if(user.getAuthorities().contains(new SimpleGrantedAuthority(PerfilTipo.PACIENTE.getDesc()))) {
			return  ResponseEntity.ok(service.buscarHistoricoPorPacienteEmail(user.getUsername(), request));
		}
		if(user.getAuthorities().contains(new SimpleGrantedAuthority(PerfilTipo.MEDICO.getDesc()))) {
			return  ResponseEntity.ok(service.buscarHistoricoPorMedicoEmail(user.getUsername(), request));
		}
		return ResponseEntity.notFound().build();
	}
	
	// localizar agendamento pelo id e envia-lo para a pagina de cadastro
	@GetMapping("/editar/consulta/{id}")
	@PreAuthorize("hasAnyAuthorizy('PACIENTE', 'MEDICO')")
	public String preEditarConsultaPaciente(@PathVariable("id") Long id,
			ModelMap model, @AuthenticationPrincipal User user) {
		
		Agendamento agendamento = service.buscarPorIdEUsuario(id, user.getUsername());
		model.addAttribute("agendamento", agendamento);
		return "agendamento/cadastro";
	}
	
	@PreAuthorize("hasAnyAuthorizy('PACIENTE', 'MEDICO')")
	@PostMapping("/editar")
	public String editarConsulta(Agendamento agendamento, RedirectAttributes attr, @AuthenticationPrincipal User user) {
		String titulo = agendamento.getEspecialidade().getTitulo();
		Especialidade especialidade = especialidadeService
				.buscarPorTitulos(new String [] {titulo})
				.stream().findFirst().get();
		agendamento.setEspecialidade(especialidade);
		service.editar(agendamento, user.getUsername());
		attr.addFlashAttribute("sucesso", "Sua consulta foi alterada com sucesso.");
		return "redirect:/agendamentos/agendar";
	}
	
	@PreAuthorize("hasAuthorizy('PACIENTE')")
	@GetMapping("/excluir/consulta/{id}")
	public String excluirConsulta(@PathVariable("id") Long id, RedirectAttributes attr) {
		service.remover(id);
		attr.addFlashAttribute("successo", "Consulta excluída com sucesso.");
		return "redirect:/agendamentos/historico/paciente";
	}
}
