package com.tblGroup.toBuyList.config;

import com.tblGroup.toBuyList.models.Client;
import com.tblGroup.toBuyList.models.Enum.Role;
import com.tblGroup.toBuyList.repositories.ClientRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {
	private final ClientRepository clientRepository;
	private final PasswordEncoder passwordEncoder;
	
	private static final String NAME_ADMIN_ONE =  "toto";
	private static final String USERNAME_ADMIN_ONE = "toto@";
	private static final String MAIL_ADMIN_ONE = "toto@gmail.com";
	private static final String PASSWORD_ADMIN_ONE = "toto237";
	private static final Role ROLE_ADMIN_ONE = Role.ADMIN;
	
	private static final String NAME_ADMIN_TWO=  "tata";
	private static final String USERNAME_ADMIN_TWO= "tata@";
	private static final String MAIL_ADMIN_TWO= "tata@gmail.com";
	private static final String PASSWORD_ADMIN_TWO= "tata237";
	private static final Role ROLE_ADMIN_TWO = Role.ADMIN;
	
	
	public DataLoader(ClientRepository clientRepository, PasswordEncoder passwordEncoder) {
		this.clientRepository = clientRepository;
		this.passwordEncoder = passwordEncoder;
	}
	
//	------------------------------------------------------------------------------------------
	@Override
	public void run(String... args) throws Exception{
		if (clientRepository.findByUsername(USERNAME_ADMIN_ONE) .isEmpty()){
			Client adminOne = new Client();
			adminOne.setName(NAME_ADMIN_ONE);
			adminOne.setUsername(USERNAME_ADMIN_ONE);
			adminOne.setMail(MAIL_ADMIN_ONE);
			adminOne.setPassword(passwordEncoder.encode(PASSWORD_ADMIN_ONE));
			adminOne.setRoleName(ROLE_ADMIN_ONE);
			
			
			clientRepository.save(adminOne);
			
			System.out.println("✅ Admin loaded successfully ! ✅");
		}
	}
}
