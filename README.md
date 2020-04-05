Currency Exchange Simulator API
===
*Project created as a part of the subject 'Databases' in AGH University of Science and Technology.*

## Team
+ [Konrad Bochnia](https://github.com/szebniok)
+ [Jakub Myśliwiec](https://github.com/jmysliv)

## Task
Our task was to implement fragment of the database application. We could choose both the topic and the technology.
## About Project
Our idea was to create application for exchange market 
enthusiasts. Application that will allow users to check informations about currency exchange rate, and to test buying some currencies in order to make a profit like on real exchange market. It would be a perfect tool for those who want to test their analytics skills and don't want to risk on the real exchange market with real money.

Technologies we decided to use was Java Spring Boot framework to create backend REST API, and MongoDB as a database.

## REST API 
### REST paths
+ [/auth/login POST](###creating-new-user)
+ [/auth/register POST](###login)
+ [/users GET](###list-of-all-users)
+ [/users/me GET](###user-data)
+ [/users/me PUT](###changing-user-amount-of-money)
+ [/currencies GET](###list-of-all-currencies)
+ [/currencies/id GET](###one-currency)
+ [/bets GET](###list-of-user-bets)
+ [/bets POST](###buy-new-currency)
+ [/bets/id GET](###one-bet)
+ [/bets/id PUT](###sell-currency)
### Paths that doesn't require authentication:
+ /auth/login POST
+ /auth/register POST
### Authentication
To obtain authentication you need to add a specific header to your request. It should look like this:
```JSON
{
    "Authorization": "Bearer <your token>"
}
```
### Creating new user
```/auth/register POST```

JSON params
```JSON
{
    "name": "<user name>",
    "email": "<user email>",
    "password": "<user password>"
}
```
Possible responses:

+ *status code 400* : Some field is missing
+ *status code 409* : User with given email exists
+ *status code 201* : User created

### Login
```/auth/login POST```

JSON params
```JSON
{
    "email": "<user email>",
    "password": "<user password>"
}
```
Possible responses:

+ *status code 400* : Invalid credentials
+ *status code 201* : 
```JSON
{
    "email": "<user email>",
    "token": "<user token>"
}
```
### List of all users
```/users GET```
Possible responses:

+ *status code 401* : Unauthorized
+ *status code 200* : 
```JSON
[
    {
        "id": "<user id>",
        "name": "<user name>",
        "email": "<user email>",
        "password": "",
        "amountOfPLN": "<user money>"
    },

]
```
### User data
```/users/me GET```

Possible responses:
+ *status code 401* : Unauthorized
+ *status code 200* : 
```JSON
{
    "id": "<user id>",
    "name": "<user name>",
    "email": "<user email>",
    "password": "",
    "amountOfPLN": "<user money>"
}
```

### Changing user amount of money
```/users/me PUT```

JSON params
```JSON
{
    "amountOfPLN": "<new amount>"
}
```
Possible responses:
+ *status code 401* : Unauthorized
+ *status code 200* : 
```JSON
{
    "id": "<user id>",
    "name": "<user name>",
    "email": "<user email>",
    "password": "",
    "amountOfPLN": "<new amount>"
}
```

### List of all currencies
```/currencies GET```

Possible responses:
+ *status code 401* : Unauthorized
+ *status code 200* : 
```JSON
[
    {
        "id": "<currency id>",
        "name": "<currency name>",
        "symbol": "<currency symbol>",
        "timestamps": [
            {
                "date": "YYYY-MM-DD",
                "exchangeRate": "<double>"
            },
        
        ]
    },

]
```

### One currency
```/currencies/id GET```

Possible responses:
+ *status code 401* : Unauthorized
+ *status code 200* : 
```JSON
{
    "id": "<currency id>",
    "name": "<currency name>",
    "symbol": "<currency symbol>",
    "timestamps": [
        {
            "date": "YYYY-MM-DD",
            "exchangeRate": "<double>"
        },
    
    ]
}
```

### List of user bets
```/bets GET```

Possible responses:
+ *status code 401* : Unauthorized
+ *status code 200* : 
```JSON
 [
    {
        "id": "<Bet id>",
        "currencyId": "<Currency id>",
        "userId": "<User id>",
        "currencySymbol": "<Symbol>",
        "amountOfCurrency": "<amountInvestedPLN * rate in the purchase date>",
        "purchaseDate": "YYYY-MM-DD",
        "soldDate": "YYYY-MM-DD, or null",
        "amountInvestedPLN": "<double>",
        "amountObtainedPLN": "amountOfCurrency * rate in sold date"
    },

 ]
```

### Buy new currency
```/bets POST```

JSON params
```JSON
{
    "currencyId": "<currency id>",
    "amountInvestedPLN": "<how much user want to invest>"
}
```
Possible responses:
+ *status code 401* : Unauthorized
+ *status code 400* : Field is missing, or incorrect currencyId
+ *status code 409* : User doesn't have enough money to invest
+ *status code 200* : 
```JSON
{
    "id": "<bet id>"
}
```

### One bet
```/bets/id GET```

Possible responses:
+ *status code 401* : Unauthorized
+ *status code 400* : Bet belongs to other user, or doesn't exist
+ *status code 200* : 
```JSON
{
    "id": "<Bet id>",
    "currencyId": "<Currency id>",
    "userId": "<User id>",
    "currencySymbol": "<Symbol>",
    "amountOfCurrency": "<amountInvestedPLN * rate in the purchase date>",
    "purchaseDate": "YYYY-MM-DD",
    "soldDate": "YYYY-MM-DD, or null",
    "amountInvestedPLN": "<double>",
    "amountObtainedPLN": "amountOfCurrency * rate in sold date"
}
```

### Sell currency
```/bets/id PUT```

Possible responses:
+ *status code 401* : Unauthorized
+ *status code 400* : Bet belongs to other user, or doesn't exist
+ *status code 409* : Bet has already been sold
+ *status code 200* : 
```JSON
{
    "id": "<Bet id>",
    "currencyId": "<Currency id>",
    "userId": "<User id>",
    "currencySymbol": "<Symbol>",
    "amountOfCurrency": "<amountInvestedPLN * rate in the purchase date>",
    "purchaseDate": "YYYY-MM-DD",
    "soldDate": "YYYY-MM-DD",
    "amountInvestedPLN": "<double>",
    "amountObtainedPLN": "amountOfCurrency * rate in sold date"
}
```