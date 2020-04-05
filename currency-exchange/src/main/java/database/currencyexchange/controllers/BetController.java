package database.currencyexchange.controllers;

import database.currencyexchange.models.Bet;
import database.currencyexchange.models.Currency;
import database.currencyexchange.models.User;
import database.currencyexchange.repositories.BetRepository;
import database.currencyexchange.repositories.CurrencyRepository;
import database.currencyexchange.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
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
    public ResponseEntity buy(@RequestBody Bet bet) {
        if(bet.getCurrencyId() == null){
            Map<Object, Object> response = new HashMap<>();
            response.put("message", "currencyId field is required");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        Optional<Currency> currency = currencyRepository.findById(bet.getCurrencyId());
        if(currency.isEmpty()){
            Map<Object, Object> response = new HashMap<>();
            response.put("message", "Currency with given id doesn't exist");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.findUserByEmail(userDetails.getUsername());
        if( bet.getAmountInvestedPLN() > user.getAmountOfPLN()){
            Map<Object, Object> response = new HashMap<>();
            response.put("message", "You don't have enough money to invest");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }

        user.setAmountOfPLN(user.getAmountOfPLN() - bet.getAmountInvestedPLN());
        userService.updateUser(user);
        bet.setUserId(user.getId());
        bet.setAmountObtainedPLN(Double.NaN);
        bet.setSoldDate(null);
        bet.setPurchaseDate(LocalDate.now());
        bet.setCurrencySymbol(currency.get().getSymbol());
        double rate = currency.get().getCurrentExchangeRate();
        bet.setAmountOfCurrency(bet.getAmountInvestedPLN() * (1/rate));
        betRepository.save(bet);

        Map<Object, Object> response = new HashMap<>();
        response.put("id", bet.getId());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(method=RequestMethod.GET, value="/{id}")
    public ResponseEntity show(@PathVariable String id) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.findUserByEmail(userDetails.getUsername());
        Optional<Bet> bet = betRepository.findById(id);
        if(bet.isPresent() && bet.get().getUserId().equals(user.getId())){
            return new ResponseEntity<>(bet, HttpStatus.OK);
        }
        Map<Object, Object> response = new HashMap<>();
        response.put("message", "Bet doesn't exist or belongs to other user");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(method=RequestMethod.PUT, value="/{id}")
    public ResponseEntity sell(@PathVariable String id) {
        Optional<Bet> oldBet = betRepository.findById(id);
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.findUserByEmail(userDetails.getUsername());
        if(oldBet.isEmpty() || !oldBet.get().getUserId().equals(user.getId())){
            Map<Object, Object> response = new HashMap<>();
            response.put("message", "Bet doesn't exist or belongs to other user");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        if(oldBet.get().getSoldDate() == null){
            oldBet.get().setSoldDate(LocalDate.now());
            Currency currency = currencyRepository.findById(oldBet.get().getCurrencyId()).get();
            double rate = currency.getCurrentExchangeRate();
            oldBet.get().setAmountObtainedPLN(oldBet.get().getAmountOfCurrency() * rate);
            user.setAmountOfPLN(oldBet.get().getAmountObtainedPLN() + user.getAmountOfPLN());
            userService.updateUser(user);
            betRepository.save(oldBet.get());
            return new ResponseEntity<>(oldBet.get(), HttpStatus.OK);
        }
        Map<Object, Object> response = new HashMap<>();
        response.put("message", "Bet has already been sold");
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

}
