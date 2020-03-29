package database.currencyexchange.repositories;

import database.currencyexchange.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
    User findUserByEmail(String name);
}
