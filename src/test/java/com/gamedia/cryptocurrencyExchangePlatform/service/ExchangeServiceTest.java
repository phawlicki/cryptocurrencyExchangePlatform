package com.gamedia.cryptocurrencyExchangePlatform.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamedia.cryptocurrencyExchangePlatform.CryptocurrencyExchangePlatformApplication;
import com.gamedia.cryptocurrencyExchangePlatform.model.CoinsRate;
import com.gamedia.cryptocurrencyExchangePlatform.model.CurrencyRate;
import com.gamedia.cryptocurrencyExchangePlatform.model.Rate;
import com.gamedia.cryptocurrencyExchangePlatform.model.RequestForExchange;
import com.gamedia.cryptocurrencyExchangePlatform.model.ResponseForExchange;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;


@RunWith(SpringRunner.class)
@ContextConfiguration(classes = CryptocurrencyExchangePlatformApplication.class)
public class ExchangeServiceTest {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ExchangeService exchangeService;

    private MockRestServiceServer mockServer;
    private ObjectMapper mapper = new ObjectMapper();

    @Before
    public void init() {
        mockServer = MockRestServiceServer.bindTo(this.restTemplate)
                .ignoreExpectOrder(true).build();
    }

    @Test
    public void shouldGetFilteredCurrencies() throws JsonProcessingException, URISyntaxException {

        //given
        String url = "https://rest.coinapi.io/v1/exchangerate/";
        String apiKey = "&apikey=E68DB979-F617-4E20-8CEC-09448DB64524";
        String baseCurrency = "BTC";
        String filterAsset = "filter_asset_id=";
        String paramToAppend = "ETH,XRP,";

        List<String> filter = Arrays.asList("ETH", "XRP");
        CoinsRate coinsRate = new CoinsRate();
        coinsRate.setBaseCurrency("BTC");
        List<CurrencyRate> listOfCurrencies = new ArrayList<>();
        CurrencyRate currencyRate = new CurrencyRate();
        currencyRate.setToCurrency("ETH");
        currencyRate.setRate(BigDecimal.valueOf(10.5));
        listOfCurrencies.add(currencyRate);
        CurrencyRate currencyRate1 = new CurrencyRate();
        currencyRate1.setToCurrency("XRP");
        currencyRate1.setRate(BigDecimal.valueOf(15.5));
        listOfCurrencies.add(currencyRate1);
        coinsRate.setRates(listOfCurrencies);

        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI(url + baseCurrency + "?" + filterAsset + paramToAppend + apiKey)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(coinsRate))
                );

        //when
        Optional<CoinsRate> result = exchangeService.getFilteredCurrencies(baseCurrency, filter);
        //then
        mockServer.verify();
        Assert.assertEquals(coinsRate.getBaseCurrency(), result.get().getBaseCurrency());
        Assert.assertEquals(coinsRate.getRates().get(0).getToCurrency(), result.get().getRates().get(0).getToCurrency());
        Assert.assertEquals(coinsRate.getRates().get(1).getToCurrency(), result.get().getRates().get(1).getToCurrency());
    }

    @Test
    public void shouldGetAllCurrencies() throws JsonProcessingException, URISyntaxException {
        //given
        String url = "https://rest.coinapi.io/v1/exchangerate/";
        String apiKey = "?apikey=E68DB979-F617-4E20-8CEC-09448DB64524";
        String baseCurrency = "BTC";

        CoinsRate coinsRate = new CoinsRate();
        coinsRate.setBaseCurrency("BTC");
        List<CurrencyRate> listOfCurrencies = new ArrayList<>();
        CurrencyRate currencyRate = new CurrencyRate();
        currencyRate.setToCurrency("ETH");
        currencyRate.setRate(BigDecimal.valueOf(10.50));
        listOfCurrencies.add(currencyRate);
        CurrencyRate currencyRate1 = new CurrencyRate();
        currencyRate1.setToCurrency("XRP");
        currencyRate1.setRate(BigDecimal.valueOf(15.50));
        listOfCurrencies.add(currencyRate1);

        CurrencyRate currencyRate2 = new CurrencyRate();
        currencyRate2.setToCurrency("BCH");
        currencyRate2.setRate(BigDecimal.valueOf(30.50).setScale(2, BigDecimal.ROUND_HALF_UP));
        listOfCurrencies.add(currencyRate2);
        coinsRate.setRates(listOfCurrencies);

        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI(url + baseCurrency + apiKey)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(coinsRate))
                );

        //when
        Optional<CoinsRate> result = exchangeService.getAllCurrencies(baseCurrency);

        //then
        mockServer.verify();
        Assert.assertEquals(coinsRate.getBaseCurrency(), result.get().getBaseCurrency());
        Assert.assertEquals(coinsRate.getRates().get(0).getToCurrency(), result.get().getRates().get(0).getToCurrency());
        Assert.assertEquals(coinsRate.getRates().get(1).getToCurrency(), result.get().getRates().get(1).getToCurrency());
        Assert.assertEquals(coinsRate.getRates().get(2).getRate(), result.get().getRates().get(2).getRate());
    }

    @Test
    public void shouldExchangeRate() throws JsonProcessingException, URISyntaxException, ExecutionException, InterruptedException {
        //given
        String url = "https://rest.coinapi.io/v1/exchangerate/";
        String apiKey = "?apikey=E68DB979-F617-4E20-8CEC-09448DB64524";

        RequestForExchange requestForExchange = new RequestForExchange();
        List<String> currencyTo = Arrays.asList("XRP", "ETH");
        requestForExchange.setFrom("BTC");
        requestForExchange.setAmount(BigDecimal.valueOf(10.0));
        requestForExchange.setTo(currencyTo);
        CurrencyRate currencyRate = new CurrencyRate();
        currencyRate.setToCurrency("XRP");
        currencyRate.setRate(BigDecimal.valueOf(10.0));
        CurrencyRate currencyRate1 = new CurrencyRate();
        currencyRate1.setToCurrency("ETH");
        currencyRate1.setRate(BigDecimal.valueOf(20.0));

        ResponseForExchange responseForExchange = new ResponseForExchange();
        responseForExchange.setFrom(requestForExchange.getFrom());
        List<Rate> rateList = new ArrayList<>();
        Rate rate = new Rate();
        rate.setAmount(BigDecimal.valueOf(10.00).setScale(2, BigDecimal.ROUND_HALF_UP));
        rate.setCurrency("XRP");
        rate.setFee(BigDecimal.valueOf(1.0).setScale(4, BigDecimal.ROUND_HALF_UP));
        rate.setRate(BigDecimal.valueOf(10.0).setScale(2, BigDecimal.ROUND_HALF_UP));
        rate.setResult(BigDecimal.valueOf(101.0).setScale(3, BigDecimal.ROUND_HALF_UP));
        rateList.add(rate);

        Rate rate1 = new Rate();
        rate1.setAmount(BigDecimal.valueOf(10.0).setScale(2, BigDecimal.ROUND_HALF_UP));
        rate1.setCurrency("ETH");
        rate1.setFee(BigDecimal.valueOf(2.0).setScale(4, BigDecimal.ROUND_HALF_UP));
        rate1.setRate(BigDecimal.valueOf(20.0).setScale(2, BigDecimal.ROUND_HALF_UP));
        rate1.setResult(BigDecimal.valueOf(202.0).setScale(3, BigDecimal.ROUND_HALF_UP));
        rateList.add(rate1);

        responseForExchange.setTo(rateList);

        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI(url + requestForExchange.getFrom() + "/" + currencyTo.get(0) + apiKey)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(currencyRate))
                );


        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI(url + requestForExchange.getFrom() + "/" + currencyTo.get(1) + apiKey)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(currencyRate1))
                );

        //when
        ResponseForExchange result = exchangeService.exchangeRate(requestForExchange);
        mockServer.verify();
        mockServer.verify();

        //then
        Assert.assertEquals(responseForExchange.getTo().get(0).getAmount(), result.getTo().get(0).getAmount());
        Assert.assertEquals(responseForExchange.getTo().get(0).getFee(), result.getTo().get(0).getFee());
        Assert.assertEquals(responseForExchange.getTo().get(0).getRate(), result.getTo().get(0).getRate());
        Assert.assertEquals(responseForExchange.getTo().get(0).getResult(), result.getTo().get(0).getResult());
        Assert.assertEquals(responseForExchange.getTo().get(1).getAmount(), result.getTo().get(1).getAmount());
        Assert.assertEquals(responseForExchange.getTo().get(1).getFee(), result.getTo().get(1).getFee());
        Assert.assertEquals(responseForExchange.getTo().get(1).getRate(), result.getTo().get(1).getRate());
        Assert.assertEquals(responseForExchange.getTo().get(1).getResult(), result.getTo().get(1).getResult());

    }
}