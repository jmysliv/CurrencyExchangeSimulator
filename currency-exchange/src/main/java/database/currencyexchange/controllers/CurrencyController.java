package database.currencyexchange.controllers;

import database.currencyexchange.models.Currency;
import database.currencyexchange.repositories.CurrencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Optional;

public class CurrencyController {
    @Autowired
    CurrencyRepository currencyRepository;

    @RequestMapping(method= RequestMethod.GET, value="/api/currencies")
    public Iterable<Currency> currencyIterable() {
        return currencyRepository.findAll();
    }

    @RequestMapping(method=RequestMethod.POST, value="/api/currencies")
    public String save(@RequestBody Currency currency) {
        currencyRepository.save(currency);

        return currency.getId();
    }

    @RequestMapping(method=RequestMethod.GET, value="/api/currencies/{id}")
    public Optional<Currency> show(@PathVariable String id) {
        return currencyRepository.findById(id);
    }

    @RequestMapping(method=RequestMethod.PUT, value="/api/currencies/{id}")
    public Currency update(@PathVariable String id, @RequestBody Currency currency) {
        Optional<Currency> c = currencyRepository.findById(id);
        if(currency.getName() != null)
            c.get().setName(currency.getName());
        if(currency.getSymbol() != null)
            c.get().setSymbol(currency.getSymbol());
        if(currency.getTimestamps() != null)
            c.get().setTimestamps(currency.getTimestamps());
        currencyRepository.save(c.get());
        return c.get();
    }

    @RequestMapping(method=RequestMethod.DELETE, value="/api/currencies/{id}")
    public String delete(@PathVariable String id) {
        Optional<Currency> currency = currencyRepository.findById(id);
        currencyRepository.delete(currency.get());

        return "currency deleted";
    }
}
