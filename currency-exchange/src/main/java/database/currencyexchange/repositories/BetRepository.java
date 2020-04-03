package database.currencyexchange.repositories;

import database.currencyexchange.models.Bet;
import org.springframework.data.repository.CrudRepository;

public interface BetRepository extends CrudRepository<Bet, String> {
}
