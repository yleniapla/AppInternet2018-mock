package it.polito.ai.server.service;

import it.polito.ai.server.model.Role;
import it.polito.ai.server.model.User;
import it.polito.ai.server.repositories.RoleRepository;
import it.polito.ai.server.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service(value = "userService")
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder userPasswordEncoder;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), getAuthorities( user.getRoles() ));
        }
        throw new UsernameNotFoundException(username);
    }

    private Collection<? extends GrantedAuthority> getAuthorities(
            Collection<Role> roles) {

        List<GrantedAuthority> authorities = new ArrayList<>();
        for (Role r : roles) {

            System.out.println("Role: " + r.getName());
            authorities.add(new SimpleGrantedAuthority(r.getName()));
        }

        return authorities;
    }

    public User register(String username, String password) throws Exception {
        User user = userRepository.findByUsername(username);
        if (user != null || username.isEmpty() || password.isEmpty()) {
            throw new Exception("This username already exists or is not admitted!");
        }

        user = new User(username, userPasswordEncoder.encode(password));
        Role userRole = new Role("ROLE_USER");
        user.addRole(userRole);
        userRepository.insert(user);

        return user;

    }

    public User login(String username, String password) throws Exception {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new Exception("Username does not exists!");
        }
        if(!user.getPassword().equals(userPasswordEncoder.encode(password))){
            throw  new Exception(("Password incorrect"));
        }
        return user;
    }



}
