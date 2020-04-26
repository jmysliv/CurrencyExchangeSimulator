package database.currencyexchange.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import database.currencyexchange.models.Currency;
import database.currencyexchange.models.Timestamp;
import database.currencyexchange.repositories.CurrencyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@EnableScheduling
@Service
public class CurrencyService {
    private final class LatestDate {
        LocalDate latest;
    }

    private CurrencyRepository currencyRepository;
    private Logger log = LoggerFactory.getLogger(CurrencyService.class);
    private MongoTemplate mongoTemplate;
    private HttpClient httpClient = HttpClient.newHttpClient();

    public CurrencyService(CurrencyRepository currencyRepository, MongoTemplate mongoTemplate) {
        this.currencyRepository = currencyRepository;
        this.mongoTemplate = mongoTemplate;
    }

    @Scheduled(cron = "0 0 18 * * *")
    @EventListener(ApplicationReadyEvent.class)
    public void fillCurrencyData() throws ExecutionException, InterruptedException {
        var futures = currencyRepository.findAll().stream()
                .map(currency -> {
                    LocalDate latestRecordedTimestamp = getLatestRecordedTimestamp(currency.getSymbol());
                    return fillMissingData(currency.getSymbol(), latestRecordedTimestamp);
                }).toArray(CompletableFuture[]::new);

        CompletableFuture.allOf(futures).get();

        log.info("Successfully filled missing data");
    }

    private LocalDate getLatestRecordedTimestamp(String symbol) {
        MatchOperation matchOperation = match(new Criteria("symbol").is(symbol));
        UnwindOperation unwindOperation = unwind("timestamps");
        GroupOperation groupOperation = group("_id")
                .max("timestamps.date").as("latest");

        Aggregation aggregation = newAggregation(matchOperation, unwindOperation, groupOperation);
        AggregationResults<LatestDate> latestDate = mongoTemplate
                .aggregate(aggregation, "currencies", LatestDate.class);

        return latestDate.getMappedResults().stream()
                .map(ld -> ld.latest)
                .findFirst()
                .orElseGet(() -> LocalDate.of(2018, 12, 31));
    }

    private CompletableFuture<Void> fillMissingData(String symbol, LocalDate latestRecordedTimestamp) {
        LocalDate today = LocalDate.now();
        if (today.equals(latestRecordedTimestamp)) return CompletableFuture.completedFuture(null);

        String startAt = latestRecordedTimestamp.plusDays(1).toString();
        String endAt = today.toString();

        URI apiUrl = UriComponentsBuilder.newInstance()
                .scheme("https")
                .host("api.exchangeratesapi.io")
                .path("history")
                .queryParam("start_at", startAt)
                .queryParam("end_at", endAt)
                .queryParam("symbols", "PLN")
                .queryParam("base", symbol)
                .build().toUri();

        HttpRequest request = HttpRequest.newBuilder().uri(apiUrl).build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
               .thenApply(HttpResponse::body)
               .thenAccept(json -> saveDataFromString(symbol, json));
    }

    private void saveDataFromString(String symbol, String json) {
        ObjectReader reader = new ObjectMapper().readerFor(Map.class);

        try {
            Map<String, Object> parsedJson = reader.readValue(json);
            Map<String, Object> rates = (Map<String, Object>) parsedJson.get("rates");

            List<Timestamp> timestamps = new ArrayList<>();
            for (var rate: rates.entrySet()) {
                LocalDate date = LocalDate.parse(rate.getKey(), DateTimeFormatter.ISO_LOCAL_DATE);
                double exchangeRate = ((Map<String, Double>) rate.getValue()).get("PLN");
                timestamps.add(new Timestamp(date, exchangeRate));
            }

            Update update = new Update();
            update.push("timestamps").each(timestamps);
            Criteria criteria = Criteria.where("symbol").is(symbol);
            mongoTemplate.updateFirst(Query.query(criteria), update, "currencies");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
