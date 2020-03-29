package database.currencyexchange.models;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
import java.util.List;

@Document(collection = "currencies")
public class Currency {

    @Id
    private String id;
    private String name;
    private String symbol;
    private List<Timestamp> timestamps;

    public Currency(String id, String name, String symbol, List<Timestamp> timestamps){
        this.id = id;
        this.name = name;
        this.symbol = symbol;
        this.timestamps = timestamps;
    }

    public List<Timestamp> getTimestamps() {
        return timestamps;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setTimestamps(List<Timestamp> timestamps) {
        this.timestamps = timestamps;
    }
}
