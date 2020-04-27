package com.gamedia.cryptocurrencyExchangePlatform.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamedia.cryptocurrencyExchangePlatform.model.CoinsRate;
import com.gamedia.cryptocurrencyExchangePlatform.model.CurrencyRate;
import com.gamedia.cryptocurrencyExchangePlatform.model.Rate;
import com.gamedia.cryptocurrencyExchangePlatform.model.RequestForExchange;
import com.gamedia.cryptocurrencyExchangePlatform.model.ResponseForExchange;
import com.gamedia.cryptocurrencyExchangePlatform.service.ExchangeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(ExchangeController.class)
public class ExchangeControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ExchangeService exchangeService;

    @Autowired
    ObjectMapper objectMapper;


    @Test
    public void shouldCalculateExchangeRate() throws Exception {
        RequestForExchange requestForExchange = new RequestForExchange();
        List<String> currencyTo = Arrays.asList("ETH", "XRP");

        requestForExchange.setFrom("BTC");
        requestForExchange.setAmount(BigDecimal.valueOf(4.0));
        requestForExchange.setTo(currencyTo);

        ResponseForExchange responseForExchange = new ResponseForExchange();
        List<Rate> rateList = new ArrayList<>();
        Rate rate = new Rate();
        rate.setResult(BigDecimal.valueOf(1000.0));
        rate.setRate(BigDecimal.valueOf(34.0));
        rate.setFee(BigDecimal.valueOf(0.5));
        rate.setAmount(BigDecimal.valueOf(4.0));
        rate.setCurrency("ETH");
        Rate rate1 = new Rate();
        rate1.setResult(BigDecimal.valueOf(500.0));
        rate1.setRate(BigDecimal.valueOf(15.0));
        rate1.setFee(BigDecimal.valueOf(0.75));
        rate1.setAmount(BigDecimal.valueOf(4.0));
        rate1.setCurrency("XRP");
        rateList.add(rate);
        rateList.add(rate1);

        responseForExchange.setFrom("BTC");
        responseForExchange.setTo(rateList);

        try {
            when(exchangeService.exchangeRate(requestForExchange)).thenReturn(responseForExchange);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String json = objectMapper.writeValueAsString(requestForExchange);
        mockMvc.perform(post("/currencies/exchange").contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(json)).andReturn();
    }


    @Test
    public void shouldGetCurrencyWhenNoFilter() throws Exception {

        String fromCurrency="BTC";

        CoinsRate coinsRate=new CoinsRate();

        List<CurrencyRate> currencyRates=new ArrayList<>();
        CurrencyRate currencyRate=new CurrencyRate();
        currencyRate.setRate(BigDecimal.valueOf(10));
        currencyRate.setToCurrency("ETH");
        currencyRates.add(currencyRate);

        CurrencyRate currencyRate1=new CurrencyRate();
        currencyRate1.setRate(BigDecimal.valueOf(20));
        currencyRate1.setToCurrency("XRP");
        currencyRates.add(currencyRate1);

        CurrencyRate currencyRate2=new CurrencyRate();
        currencyRate2.setRate(BigDecimal.valueOf(30));
        currencyRate2.setToCurrency("BCH");
        currencyRates.add(currencyRate2);

        coinsRate.setRates(currencyRates);
        coinsRate.setBaseCurrency("BTC");
        Optional<CoinsRate> result=Optional.of(coinsRate);

        when(exchangeService.getAllCurrencies(fromCurrency)).thenReturn(result);

        this.mockMvc.perform(get("/currencies/{fromCurrency}", fromCurrency)).andDo(print()).andExpect(status().isOk()).
                andExpect(MockMvcResultMatchers.jsonPath("$.baseCurrency").value("BTC"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.rates[0].toCurrency").value("ETH"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.rates[0].rate").value(10));
    }

    @Test
    public void shouldGetCurrencyWhenFilterExists() throws Exception {

        String fromCurrency="BTC";
        CoinsRate coinsRate=new CoinsRate();
        List<CurrencyRate> currencyRates=new ArrayList<>();
        CurrencyRate currencyRate=new CurrencyRate();
        currencyRate.setRate(BigDecimal.valueOf(10));
        currencyRate.setToCurrency("ETH");
        currencyRates.add(currencyRate);

        CurrencyRate currencyRate1=new CurrencyRate();
        currencyRate1.setRate(BigDecimal.valueOf(20));
        currencyRate1.setToCurrency("XRP");
        currencyRates.add(currencyRate1);

        coinsRate.setRates(currencyRates);
        coinsRate.setBaseCurrency("BTC");
        Optional<CoinsRate> result=Optional.of(coinsRate);

        List<String> filter=Arrays.asList("ETH", "XRP");

        when(exchangeService.getFilteredCurrencies(fromCurrency, filter)).thenReturn(result);
        this.mockMvc.perform(get("/currencies/{fromCurrency}", fromCurrency).param("filter",filter.get(0)).param("filter",filter.get(1))).andDo(print()).andExpect(status().isOk()).
                andExpect(MockMvcResultMatchers.jsonPath("$.baseCurrency").value("BTC"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.rates[0].toCurrency").value("ETH"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.rates[0].rate").value(10));

    }
}