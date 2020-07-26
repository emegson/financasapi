package br.com.controle.financeiro.service;

import br.com.controle.financeiro.model.entity.UserEntity;

import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    UserEntity registerUser(RegisterUserInit init);

    class RegisterUserInit {
        private final String userName;
        private final String email;

        public RegisterUserInit(String userName, String email) {
            super();
            this.userName = userName;
            this.email = email;
        }

        public String getUserName() {
            return userName;
        }

        public String getEmail() {
            return email;
        }

    }

}