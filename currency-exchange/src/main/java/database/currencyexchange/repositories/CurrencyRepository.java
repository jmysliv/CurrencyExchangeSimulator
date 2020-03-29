package database.currencyexchange.repositories;

import database.currencyexchange.models.Currency;
import org.springframework.data.repository.CrudRepository;

public interface CurrencyRepository extends CrudRepository<Currency, String> {
}
