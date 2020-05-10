package br.com.controle.financeiro.service.impl;

import org.springframework.stereotype.Service;

import br.com.controle.financeiro.component.FirebaseParser;
import br.com.controle.financeiro.configuration.auth.firebase.FirebaseTokenHolder;
import br.com.controle.financeiro.service.FirebaseService;
import br.com.controle.financeiro.spring.conditionals.FirebaseCondition;

@Service
@FirebaseCondition
public class FirebaseServiceImpl implements FirebaseService {
	@Override
	public FirebaseTokenHolder parseToken(String firebaseToken) {
		return new FirebaseParser().parseToken(firebaseToken);
	}
}