package it.polito.ai.server;

import it.polito.ai.server.model.Role;
import it.polito.ai.server.model.User;
import it.polito.ai.server.repositories.ArchiveRepository;
import it.polito.ai.server.repositories.RoleRepository;
import it.polito.ai.server.repositories.UserRepository;
//import it.polito.ai.server.service.PositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Collection;

@SpringBootApplication
public class Lab3Application implements CommandLineRunner{

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private RoleRepository roleRepo;

	@Autowired
	private UserRepository usersRepo;

	@Autowired
	private ArchiveRepository archRepo;

	public static void main(String[] args) {
		SpringApplication.run(Lab3Application.class, args);
	}

	@Override
	public void run(String... args) throws Exception {}
}
