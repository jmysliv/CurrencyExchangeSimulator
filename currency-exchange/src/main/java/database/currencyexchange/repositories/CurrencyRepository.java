package database.currencyexchange.repositories;

import database.currencyexchange.models.Currency;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;

public interface CurrencyRepository extends CrudRepository<Currency, String> {
    Collection<Currency> findAll();
}
