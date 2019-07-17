package it.polito.ai.server.repositories;

import it.polito.ai.server.model.Role;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface RoleRepository extends MongoRepository<Role, String>{

}
