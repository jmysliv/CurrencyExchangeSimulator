from bs4 import BeautifulSoup
import requests
from pymongo import MongoClient

client = MongoClient('mongodb://localhost/')
db = client['currencyExchange']

def get_currencies_names_and_symbols():
    response = requests.get('https://www.ecb.europa.eu/stats/policy_and_exchange_rates/euro_reference_exchange_rates/html/index.en.html')
    currencies = []
    content = response.text
    soup = BeautifulSoup(content, "html.parser")
    table = soup.find('table', class_="ecb-forexTable fullWidth")
    for row in table.tbody.findAll('tr'):
        try:
            symbol = row.td.a.text
            name = row.td.findNext('td').a.text
            currency = {
                "name": name,
                "symbol": symbol,
                "timestamps": []
            }
            if symbol !='PLN':
                currencies.append(currency)
        except Exception as e:
            print(e)
            pass
    currency = {
        "name": "Euro Member Countries",
        "symbol": "EUR",
        "timestamps": []
    }
    currencies.append(currency)

    db.drop_collection(db.currencies)
    for currency in currencies:
        db.currencies.insert_one(currency)
  
get_currencies_names_and_symbols()
