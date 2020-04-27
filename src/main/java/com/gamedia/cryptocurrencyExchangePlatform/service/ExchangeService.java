package com.gamedia.cryptocurrencyExchangePlatform.service;

import com.gamedia.cryptocurrencyExchangePlatform.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static java.util.stream.Collectors.toList;

@Service
public class ExchangeService {
    @Autowired
    RestTemplate restTemplate;

    @Autowired
    private AsyncService asyncService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ExchangeService.class);

    public ResponseForExchange exchangeRate(RequestForExchange requestForExchange) throws ExecutionException, InterruptedException {


        List<String> listOfCurrencies = requestForExchange.getTo();
        ResponseForExchange responseForExchange = new ResponseForExchange();
        List<Rate> rates = new ArrayList<>();

        List<CompletableFuture<Rate>> completableFutures = listOfCurrencies.stream().map(currency -> asyncService.getRatesAsync(requestForExchange, currency)).collect(toList());
        CompletableFuture<List<Rate>> futureOfList = CompletableFuture
                .allOf(completableFutures.toArray(new CompletableFuture[0]))
                .thenApply(v -> completableFutures.stream().map(CompletableFuture::join).collect(toList()));

        List<Rate> newRates = futureOfList.get();
        responseForExchange.setTo(newRates);
        responseForExchange.setFrom(requestForExchange.getFrom());
        return responseForExchange;
    }


    public Optional<CoinsRate> getAllCurrencies(String baseCurrency) {
        String url = "https://rest.coinapi.io/v1/exchangerate/";
        String apiKey = "?apikey=E68DB979-F617-4E20-8CEC-09448DB64524";

        Optional<CoinsRate> coinsRate = Optional.of(restTemplate.getForObject(
                url + baseCurrency + apiKey,
                CoinsRate.class));

        if (coinsRate.isPresent()) {
            return Optional.of(round(coinsRate.get()));
        } else {
            return coinsRate;
        }

    }

    public Optional<CoinsRate> getFilteredCurrencies(String baseCurrency, List<String> filter) {

        String url = "https://rest.coinapi.io/v1/exchangerate/";
        String apiKey = "&apikey=E68DB979-F617-4E20-8CEC-09448DB64524";
        StringBuilder sb = new StringBuilder();
        String paramToAppend;
        String filterAsset = "filter_asset_id=";

        if (filter.size() > 1) {
            for (String param : filter) {
                sb.append(param + ",");
            }
            paramToAppend = sb.toString();
        } else {
            paramToAppend = filter.get(0);
        }
        Optional<CoinsRate> filteredCurrencies = Optional.of(restTemplate.getForObject(
                url + baseCurrency + "?" + filterAsset + paramToAppend + apiKey, CoinsRate.class));
        if (filteredCurrencies.isPresent()) {
            return Optional.of(round(filteredCurrencies.get()));
        } else {
            return filteredCurrencies;
        }
    }

    private CoinsRate round(CoinsRate coinsRate) {
        CoinsRate convertedCoins = new CoinsRate();
        List<CurrencyRate> ratesListWithNoRound = coinsRate.getRates();
        List<CurrencyRate> rateListWithRounding = new ArrayList<>();
        for (CurrencyRate rate : ratesListWithNoRound) {
            CurrencyRate convertedRate = new CurrencyRate();
            convertedRate.setToCurrency(rate.getToCurrency());
            if (rate.getRate().compareTo(BigDecimal.valueOf(1)) == 1) {
                convertedRate.setRate(rate.getRate().setScale(2, BigDecimal.ROUND_HALF_UP));
            } else {
                convertedRate.setRate(rate.getRate());
            }
            rateListWithRounding.add(convertedRate);
        }
        convertedCoins.setBaseCurrency(coinsRate.getBaseCurrency());
        convertedCoins.setRates(rateListWithRounding);
        return convertedCoins;
    }
}
