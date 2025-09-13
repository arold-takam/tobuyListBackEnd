package com.tblGroup.toBuyList.services;

import com.tblGroup.toBuyList.models.Client;
import com.tblGroup.toBuyList.repositories.ClientRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {


    private final ClientRepository clientRepository;
    public MyUserDetailsService(ClientRepository clientRepository){
        this.clientRepository = clientRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Client client = clientRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

            return User
                    .withUsername(client.getUsername())
                    .password(client.getPassword())
                    .authorities(new SimpleGrantedAuthority("ROLE_USER"))
                    .build();
    }
}

