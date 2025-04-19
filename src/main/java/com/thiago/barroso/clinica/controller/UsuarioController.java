package com.thiago.barroso.clinica.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.thiago.barroso.clinica.domain.Medico;
import com.thiago.barroso.clinica.domain.Perfil;
import com.thiago.barroso.clinica.domain.PerfilTipo;
import com.thiago.barroso.clinica.domain.Usuario;
import com.thiago.barroso.clinica.service.MedicoService;
import com.thiago.barroso.clinica.service.UsuarioService;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("u")
public class UsuarioController {
	
	@Autowired
	private UsuarioService service;
	
	@Autowired
	private MedicoService medicoService;
	
	//Abrir acadastro de usuários (medico/admin/paciente)
	@GetMapping("/novo/cadastro/usuario")
	public String cadastroPorAdminParaAdminMedicoPaciente(Usuario usuario) {
		return "usuario/cadastro";
	}
	
	//Abrir lista de usuários
	@GetMapping("/lista")
	public String listarUsuarios() {
		return "usuario/lista";
	}
	
	//Listar Usuarios na datatables
	@GetMapping("/datatables/server/usuarios")
	public ResponseEntity<?> listarUsuariosDatatables(HttpServletRequest request){
		return ResponseEntity.ok(service.buscarTodos(request));
	}
	
	//Salvar cadastro de usuários por administrador
	@PostMapping("/cadastro/salvar")
	public String salvarUsuarios(Usuario usuario, RedirectAttributes attr) {
		List<Perfil> perfis = usuario.getPerfis();
		if(perfis.size() > 2 ||
				perfis.containsAll(Arrays.asList(new Perfil(1L), new Perfil(3L))) ||
				perfis.containsAll(Arrays.asList(new Perfil(2L), new Perfil(3L)))){
			attr.addFlashAttribute("falha", "Paciente não pode ser Admin e/ou Médico.");
			attr.addFlashAttribute("usuario", usuario);
		}else {
			try {
				service.salvarUsuario(usuario);
				attr.addFlashAttribute("sucesso", "Operação realizada com sucesso!");
			}catch(DataIntegrityViolationException ex) {
				attr.addFlashAttribute("falha", "Cadastro não realizado, email já existente.");
			}
			
		}
		return "redirect:/u/novo/cadastro/usuario";
	}
	//Pré edicao credenciais de usuarios
	@GetMapping("/editar/credenciais/usuario/{id}")
	public ModelAndView preEditarCredenciais(@PathVariable("id") Long id) {
		return new ModelAndView("/usuario/cadastro", "usuario", service.buscarPorId(id));
	}
	
	//Pre edicao de cadastro de dados pessoais de usuarios
	@GetMapping("/editar/dados/usuario/{id}/perfis/{perfis}")
	public ModelAndView preEditarCadastroDadosPessoais(@PathVariable("id") Long usuarioId,
			@PathVariable("perfis") Long[] perfisId) {
		
		Usuario us = service.buscarPorIdEPerfis(usuarioId, perfisId);
		
		if(us.getPerfis().contains(new Perfil(PerfilTipo.ADMIN.getCod())) &&
				!us.getPerfis().contains(new Perfil(PerfilTipo.MEDICO.getCod()))) {
			return new ModelAndView("usuario/cadastro", "usuario", us);
		}else if(us.getPerfis().contains(new Perfil(PerfilTipo.MEDICO.getCod()))) {
			Medico medico = medicoService.buscarPorUsuarioId(usuarioId);
			return medico.hasNotId()
					? new ModelAndView("medico/cadastro", "medico", new Medico(new Usuario(usuarioId)))
					: new ModelAndView("medico/cadastro", "medico", medico);
		}else if(us.getPerfis().contains(new Perfil(PerfilTipo.PACIENTE.getCod()))) {
			ModelAndView model = new ModelAndView("error");
			model.addObject("status", 403);
			model.addObject("error", "Área Restrita");
			model.addObject("message", "Os dados de pacientes são restritos a ele.");
			return model;
		}
		
		return new ModelAndView("redirect:/u/lista");
	}
	
	@GetMapping("/editar/senha")
	public String abrirEditarSenha() {
		return "usuario/editar-senha";
	}
	
	@PostMapping("/confirmar/senha")
	public String editarSenha(@RequestParam("senha1") String s1, @RequestParam("senha2") String s2,
			@RequestParam("senha3") String s3, @AuthenticationPrincipal User user,
			RedirectAttributes attr) {
		
		if(!s1.equals(s2)) {
			attr.addFlashAttribute("falha", "Senhas não conferem, tente novamente");
			return "redirect:/u/editar/senha";
		}
		
		Usuario u = service.buscarPorEmail(user.getUsername());
		
		if(!UsuarioService.isSenhaCorreta(s3, u.getSenha())) {
			attr.addFlashAttribute("falha", "Senha atual não confere, tente novamente");
			return "redirect:/u/editar/senha";
		}
		
		service.alterarSenha(u, s1);
		attr.addFlashAttribute("sucesso", "Senhas alterada com suceso");
		return "redirect:/u/editar/senha";
	}
	
	// abrir página de novo cadastro de pacinte
	@GetMapping("/novo/cadastro")
	public String novoCadastro(Usuario usuario) {
		return "cadastrar-se";
	}
	
	// página de resposta de cadastro de paciente
	@GetMapping("/cadastro/realizado")
	public String cadastroRealizado() {
		return "/fragments/mensagem";
	}
	
	// recebe o form da página cadastrar-se
	@PostMapping("/cadastro/paciente/salvar")
	public String salvarCadastroPaciente(Usuario usuario, BindingResult result) throws MessagingException {
		try {
			service.salvarCadastroPaciente(usuario);
		}catch(DataIntegrityViolationException ex) {
			result.reject("email", "Ops . . . Esta e-mail já existe na base de dados.");
			return "cadastrar-se";
		}
		return "redirect:/u/cadastro/realizado";
	}
}
