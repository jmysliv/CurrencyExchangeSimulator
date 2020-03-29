package database.currencyexchange.models;

import java.util.Date;

public class Timestamp {
    private Date date;
    private double exchangeRate;

    public Timestamp(final Date date, final double exchangeRate) {
        this.date = date;
        this.exchangeRate = exchangeRate;
    }

    public Date getDate() {
        return date;
    }

    public double getExchangeRate() {
        return exchangeRate;
    }
}

