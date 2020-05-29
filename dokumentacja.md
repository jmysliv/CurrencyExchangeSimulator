# Struktura bazy

W bazie MongoDB zostały stworzone trzy kolekcję. We wszystkich dokumentach
pole `_class` jest polem dodanym przez bibliotekę Spring Data MongoDB

## Users

Kolekcja `Users` zawiera dokumenty o następującej strukturze:

```
{
    "_id" : ObjectId("5e87714104652a3d46d940b4"),
    "email" : "admin@admin.com",
    "password" : "!@#",
    "name" : "Admin",
    "amountOfPLN" : 1000.0,
    "_class" : "database.currencyexchange.models.User"
}
```

- **_id** - identyfikator użytkownika
- **email** - Email użytkownika
- **password** - Hash hasła
- **Name** - Wyświetlana nazwa
- **amountOfPLN** - Ilość złotówek którą posiada użytkownik

## Currencies

Kolekcja `Currencies` zawiera dokumenty o następującej strukturze:

```
{
    "_id" : ObjectId("5ecf8aee399590a64aaa5127"),
    "name" : "US dollar",
    "symbol" : "USD",
    "timestamps" : [ 
        {
            "date" : ISODate("2019-06-27T22:00:00.000Z"),
            "exchangeRate" : 3.7342706503,
            "_class" : "database.currencyexchange.models.Timestamp"
        }, 
        {
            "date" : ISODate("2020-05-18T22:00:00.000Z"),
            "exchangeRate" : 4.1561643836,
            "_class" : "database.currencyexchange.models.Timestamp"
        }, 
        ...
    ]
}
```

- **_id** - identyfikator waluty
- **name** - Pełna nazwa waluty
- **symbol** - Kod ISO 4217 waluty
- **timestamps** - Tablica zawierająca informację o wycenach:
- **timestamps[].date** - Data danej wyceny
- **timestamps[].exchangeRate** - Wycena jednej jednostki waluty w złotówkach

## Bets

Kolekcja `Bets` zawiera dokumenty o następującej strukturze:

```
{
    "_id" : ObjectId("5ecf99efa73a4d5f66aa701d"),
    "currencyId" : "5ecf8aee399590a64aaa5127",
    "userId" : "5e87714104652a3d46d940b4",
    "currencySymbol" : "USD",
    "amountOfCurrency" : 247.701253045412,
    "purchaseDate" : ISODate("2020-05-27T22:00:00.000Z"),
    "soldDate" : ISODate("2020-05-28T22:00:00.000Z"),
    "amountInvestedPLN" : 1000.0,
    "amountObtainedPLN" : 994.807447103792,
    "_class" : "database.currencyexchange.models.Bet"
}
```

- **_id** - identyfikator kupna
- **currencyId** - identyfikator zakupionej waluty
- **userId** - identyfikator użytkownika
- **currencySymbol** - Kod ISO 4217 waluty
- **amountOfCurrency** - Ilość zakupionej waluty
- **purchaseDate** - Data kupna
- **soldDate** - Data sprzedaży
- **amountInvestedPLN** - Ilośc wydanych złotówek w momencie kupna
- **amountObtainedPLN** - Ilość uzyskanych złotówek w momencie sprzedaży

# Struktura kodu

## src/resources/application.properties

Plik konfiguracyjny w którym musimy wskazać adres i port pod którym znajduję się baza MongoDB.

Przykładowa konfiguracja:

```properties
spring.data.mongodb.database=currencyExchange
spring.data.mongodb.host=localhost
spring.data.mongodb.port=27017
server.port=8080
```

## src/main/java

W tym folderze znajduję się cały kod Javowy.

### database.currencyexchange.configs

Moduł w którym zdefiniowana jest konfiguracja aplikacji Spring.

Zdefiniowane klasy:

- **JwtConfigurer** - ustawia wywołanie filtru `JwtFilter` przed filtrem `UsernamePasswordAuthenticationFilter`
- **JwtConfigurer** - Filtr sprawdzający poprawność tokenu JWT
- **JwtTokenProvider** - Tworzy sekretny klucz potrzebny do podpisywania tokenów JWT
- **WebSecurityConfig** - Ustawia wymagania autoryzacji dla poszczególnych endpointów

### database.currencyexchange.models

W tym module zdefiniowane są klasy które mapowane są do poszczególnych kolekcji z bazy MongoDB

- **Bet** - Mapuje dokument z kolekcji `Bets`
- **Currency** - Mapuje dokument z kolekcji `Currencies`
- **User** - Mapuje dokument z kolekcji `Users`
- **Timestamp** - Mapuje obiekt w tablicy `timestamps` z kolekcji `Currencies`

### database.currencyexchange.repositories

W tym module zdefiniowane są klasy implementujące wzorzec DAO umożliwiające dostęp do bazy

- **BetRepository** - Umożliwia dostęp do kolekcji `Bets`
- **CurrencyRepository** - Umożliwia dostęp do kolekcji `Currencies`
- **UserRepository** - Umożliwia dostęp do kolekcji `Users`

### database.currencyexchange.services

- **CurrencyService** - wypełnia bazę brakującymi wartościami walut po uruchomieniu aplikacji
- **UserService** - zawiera funkcję pomagające przy rejestracji i logowaniu i funkcję przykrywającą hasło przed zkonwertowaniem wyników do JSONa

### database.currencyexchange

Zawiera klasę`CurrencyExchangeApplication` która uruchamia cały serwer

# Tworzenie początkowych danych

## Tworzenie użytkowników
W celu stworzenia nowego użytkownika musimy wywołać endpoint `POST /auth/register` https://github.com/jmysliv/CurrencyExchangeSimulator#creating-new-user

## Pobieranie kursów danych

Klasa `CurrencyService` automatycznie wypełni naszą bazę kursami które pojawiły się od ostatniego uruchomienia.
Dane pobierane są za pomocą API https://exchangeratesapi.io/. W bazie muszą istnieć wcześniej stworzone dokumenty
w kolekcji `Currencies` definiujące waluty

## Pobieranie walut

W celu pobrania i stworzenia w bazie dokumentów dla każdej waluty stworzony został skrypt w języku Python 3
o nazwie `currency_generator.py`. Skrypt pobiera waluty widniejące na stronie europejskiego
banku centralnego https://www.ecb.europa.eu/stats/policy_and_exchange_rates/euro_reference_exchange_rates/html/index.en.html

# Frontend

Frontend został zrealizowany jako aplikacja mobilna za pomocą języka Dart i biblioteki Flutter.
Link do repozytorium: https://github.com/jmysliv/exchange-simulator
