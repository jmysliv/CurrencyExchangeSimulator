package database.currencyexchange.controllers;

import database.currencyexchange.models.Currency;
import database.currencyexchange.repositories.CurrencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/currencies")
public class CurrencyController {
    @Autowired
    CurrencyRepository currencyRepository;

    @RequestMapping(method= RequestMethod.GET)
    public Iterable<Currency> currencyIterable() {
        return currencyRepository.findAll();
    }


    @RequestMapping(method=RequestMethod.GET, value="/{id}")
    public Optional<Currency> show(@PathVariable String id) {
        return currencyRepository.findById(id);
    }


}
