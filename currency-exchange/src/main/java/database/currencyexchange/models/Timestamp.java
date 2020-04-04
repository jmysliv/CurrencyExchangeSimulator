package database.currencyexchange.models;


import java.time.LocalDate;

public class Timestamp {
    private LocalDate date;
    private double exchangeRate;

    public Timestamp(final LocalDate date, final double exchangeRate) {
        this.date = date;
        this.exchangeRate = exchangeRate;
    }

    public LocalDate getDate() {
        return date;
    }

    public double getExchangeRate() {
        return exchangeRate;
    }
}

