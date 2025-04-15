package com.thiago.barroso.clinica.projection;

import com.thiago.barroso.clinica.domain.Especialidade;
import com.thiago.barroso.clinica.domain.Medico;
import com.thiago.barroso.clinica.domain.Paciente;

public interface HistoricoPaciente {

	Long getId();
	
	Paciente getPaciente();
	
	String getDataConsulta();
	
	Medico getMedico();
	
	Especialidade getEspecialidade();
	
}
