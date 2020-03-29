package database.currencyexchange.models;


import com.fasterxml.jackson.databind.JsonSerializer;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "bets")
public class Bet {

    @Id
    private String id;
    private String currencyId;
    private String userId;
    private String currencySymbol;
    private double amountOfCurrency;
    private Date purchaseDate;
    private Date soldDate;
    private double amountInvestedPLN;
    private double amountObtainedPLN;

    public Bet(String id, String currencyId, String userId, String currencySymbol, double amountOfCurrency, Date purchaseDate, double amountInvestedPLN){
        this.id = id;
        this.currencyId = currencyId;
        this.userId = userId;
        this.currencySymbol = currencySymbol;
        this.amountOfCurrency = amountOfCurrency;
        this.purchaseDate = purchaseDate;
        this.amountInvestedPLN = amountInvestedPLN;
        this.soldDate = null;
        this.amountObtainedPLN = Double.NaN;
    }

    public String getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(String currencyId) {
        this.currencyId = currencyId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

    public double getAmountOfCurrency() {
        return amountOfCurrency;
    }

    public void setAmountOfCurrency(double amountOfCurrency) {
        this.amountOfCurrency = amountOfCurrency;
    }

    public Date getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(Date purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public Date getSoldDate() {
        return soldDate;
    }

    public void setSoldDate(Date soldDate) {
        this.soldDate = soldDate;
    }

    public double getAmountInvestedPLN() {
        return amountInvestedPLN;
    }

    public void setAmountInvestedPLN(double amountInvestedPLN) {
        this.amountInvestedPLN = amountInvestedPLN;
    }

    public double getAmountObtainedPLN() {
        return amountObtainedPLN;
    }

    public void setAmountObtainedPLN(double amountObtainedPLN) {
        this.amountObtainedPLN = amountObtainedPLN;
    }
}
