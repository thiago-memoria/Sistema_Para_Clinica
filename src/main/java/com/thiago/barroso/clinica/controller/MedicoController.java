package com.thiago.barroso.clinica.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.thiago.barroso.clinica.domain.Medico;
import com.thiago.barroso.clinica.domain.Usuario;
import com.thiago.barroso.clinica.service.MedicoService;
import com.thiago.barroso.clinica.service.UsuarioService;

@Controller
@RequestMapping("medicos")
public class MedicoController {
	
	@Autowired
	private MedicoService service;
	
	@Autowired
	private UsuarioService usuarioService;
	
	// abrir pagina de dados pessoais de medicos pelo MÈDICO
	@GetMapping({"/dados"})
	public String abrirPorMedico(Medico medico, ModelMap model, @AuthenticationPrincipal User user) {
		if(medico.hasNotId()) {
			medico = service.buscarPorEmail(user.getUsername());
			model.addAttribute("medico", medico);
		}
		return"medico/cadastro";
	}
	
	// salvar médico
	@PostMapping({"/salvar"})
	public String slvar(Medico medico, RedirectAttributes attr, @AuthenticationPrincipal User user) {
		if(medico.hasNotId() && medico.getUsuario().hasNotId()) {
			Usuario usuario = usuarioService.buscarPorEmail(user.getUsername());
			medico.setUsuario(usuario);
		}
		service.salvar(medico);
		attr.addFlashAttribute("sucesso", "Operação realizada com sucesso.");
		attr.addFlashAttribute("medico", medico);
		return"redirect:/medico/dados";
	}
	
	// editar médico
	@PostMapping({"/editar"})
	public String editar(Medico medico, RedirectAttributes attr) {
		service.editar(medico);
		attr.addFlashAttribute("sucesso", "Operação realizada com sucesso.");
		attr.addFlashAttribute("medico", medico);
		return"redirect:/medico/dados";
	}
	
	// excluir especialidades
	@GetMapping({"/id/{idMed}/excluir/especializacao/{idEsp}"})
	public String editar(@PathVariable("idMed") Long idMed,
						 @PathVariable("idEsp") Long idEsp, RedirectAttributes attr) {
		service.excluirEspecialidadesPorMedico(idMed, idEsp);
		attr.addFlashAttribute("sucesso", "Especialidade removida com sucesso.");
		return "redirect:/medicos/dados";
	}
	
}
