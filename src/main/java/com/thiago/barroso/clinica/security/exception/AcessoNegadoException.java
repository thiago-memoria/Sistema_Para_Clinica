package com.thiago.barroso.clinica.security.exception;

@SuppressWarnings("serial")
public class AcessoNegadoException extends RuntimeException{

	public AcessoNegadoException(String message) {
		super(message);
	}

}
