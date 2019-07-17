package it.polito.ai.server.repositories;

import it.polito.ai.server.model.Archive;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ArchiveRepository extends MongoRepository<Archive, ObjectId> {
}
