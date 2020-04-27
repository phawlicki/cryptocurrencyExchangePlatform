# cryptocurrencyExchangePlatform
* [General info](#general-info)
* [Technologies](#technologies)
* [Setup](#setup)
* [Examples](#setup)
* [Author](#author)


## General info
This project is simple Cryptocurrency Exchange app which can handle:
* getting actual list of currency quotations
* calculation of exchange forecast for given currency with 1% exchange fee (asynchronously)
	
## Technologies
Project is created with:
* Spring Boot
* Java 1.8
	
## Setup
To run this project execute:

* git clone --recursive https://github.com/phawlicki/cryptocurrencyExchangePlatform.git
* cd cryptocurrencyExchangePlatform
* mvn install


## Examples
* Example link do get filtered rates of currencies 
http://localhost:8081/currencies/BTC?filter=ETH&filter=XRP&filter=USDT

* Exmaples json to calculate exchange rate of 1 BTC to ETH and XRP
{
    "from": "BTC",
    "to": [
        "ETH",
        "XRP"
    ],
    "amount": 1
}

Please keep in mind that provided free api key can only handle 100 request per 24H.

## Author
* Przemysław Hawlicki
