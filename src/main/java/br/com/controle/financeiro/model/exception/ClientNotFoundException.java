package br.com.controle.financeiro.model.exception;

public class ClientNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ClientNotFoundException(Long id) {
		super("Could not find client " + id);
	}

}
