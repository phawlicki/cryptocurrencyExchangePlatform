package com.gamedia.cryptocurrencyExchangePlatform.service;

import com.gamedia.cryptocurrencyExchangePlatform.model.CurrencyRate;
import com.gamedia.cryptocurrencyExchangePlatform.model.Rate;
import com.gamedia.cryptocurrencyExchangePlatform.model.RequestForExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.CompletableFuture.completedFuture;

@Service
public class AsyncService {
    @Autowired
    RestTemplate restTemplate;
    private static final Logger LOGGER = LoggerFactory.getLogger(ExchangeService.class);

    @Async("asyncExecutor")
    public CompletableFuture<Rate> getRatesAsync(RequestForExchange requestForExchange, String currencyTo) {

        String url = "https://rest.coinapi.io/v1/exchangerate/";
        String apiKey = "?apikey=E68DB979-F617-4E20-8CEC-09448DB64524";
        LOGGER.info("Looking up " + currencyTo);
        CurrencyRate currency = restTemplate.getForObject(
                url + requestForExchange.getFrom() + "/" + currencyTo + apiKey,
                CurrencyRate.class);

        BigDecimal retrievedRate = currency.getRate().setScale(2, RoundingMode.HALF_UP);
        BigDecimal amount = requestForExchange.getAmount().setScale(2, RoundingMode.HALF_UP);
        BigDecimal fee = calculateFee(retrievedRate, amount).setScale(4, RoundingMode.HALF_UP);
        BigDecimal resultNoFee = calculateWithNoFee(retrievedRate, amount);
        BigDecimal result = finalResult(resultNoFee, fee).setScale(3, RoundingMode.HALF_UP);

        Rate rate = new Rate();
        rate.setCurrency(currencyTo);
        rate.setAmount(amount);
        rate.setRate(retrievedRate);
        rate.setFee(fee);
        rate.setResult(result);

        LOGGER.info("Retrieved " + currencyTo);
        return completedFuture(rate);
    }

    private BigDecimal calculateWithNoFee(BigDecimal rate, BigDecimal amount) {
        return rate.multiply(amount);
    }

    private BigDecimal calculateFee(BigDecimal rate, BigDecimal amount) {
        BigDecimal resultNoFee = calculateWithNoFee(rate, amount);
        BigDecimal fee = resultNoFee.multiply(BigDecimal.valueOf(0.01));
        return fee;
    }

    private BigDecimal finalResult(BigDecimal resultNoFee, BigDecimal fee) {
        return resultNoFee.add(fee);
    }
}
