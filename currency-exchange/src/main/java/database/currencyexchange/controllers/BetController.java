package database.currencyexchange.controllers;

import database.currencyexchange.models.Bet;
import database.currencyexchange.models.Timestamp;
import database.currencyexchange.models.User;
import database.currencyexchange.repositories.BetRepository;
import database.currencyexchange.repositories.CurrencyRepository;
import database.currencyexchange.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


@RestController
@RequestMapping("/bets")
public class BetController {
    @Autowired
    BetRepository betRepository;
    @Autowired
    CurrencyRepository currencyRepository;
    @Autowired
    UserService userService;

    @RequestMapping(method= RequestMethod.GET)
    public Iterable<Bet> currencyIterable() {
        Iterable<Bet> bets = betRepository.findAll();
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.findUserByEmail(userDetails.getUsername());
        return  StreamSupport.stream(bets.spliterator(), false).filter((bet) -> user.getId().equals(bet.getUserId())).collect(Collectors.toList());
    }

    @RequestMapping(method=RequestMethod.POST)
    public String save(@RequestBody Bet bet) {
        betRepository.save(bet);

        return bet.getId();
    }

    @RequestMapping(method=RequestMethod.GET, value="/{id}")
    public Optional<Bet> show(@PathVariable String id) {
        return betRepository.findById(id);
    }

    @RequestMapping(method=RequestMethod.PUT, value="/{id}")
    public Bet update(@PathVariable String id, @RequestBody Bet bet) {
        Optional<Bet> oldBet = betRepository.findById(id);
        if(bet.getSoldDate() != null){
            oldBet.get().setSoldDate(bet.getSoldDate());
            List<Timestamp> timestamps = currencyRepository.findById(oldBet.get().getCurrencyId()).get().getTimestamps();
            double rate = 0;
            for(Timestamp timestamp : timestamps){
                if(timestamp.getDate().compareTo(bet.getSoldDate()) == 0){
                    rate = timestamp.getExchangeRate();
                    break;
                }
            }
            oldBet.get().setAmountObtainedPLN(oldBet.get().getAmountOfCurrency() * rate);
        }

        betRepository.save(oldBet.get());
        return oldBet.get();
    }

}
