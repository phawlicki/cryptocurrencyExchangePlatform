package com.gamedia.cryptocurrencyExchangePlatform.rest;


import com.gamedia.cryptocurrencyExchangePlatform.model.*;
import com.gamedia.cryptocurrencyExchangePlatform.service.ExchangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/currencies")
public class ExchangeController {

    @Autowired
    private ExchangeService exchangeService;

    @PostMapping("/exchange")
    public ResponseEntity<ResponseForExchange> calculateExchangeRate(@RequestBody RequestForExchange requestForExchange) throws ExecutionException, InterruptedException {
        ResponseForExchange responseForExchange = exchangeService.exchangeRate(requestForExchange);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                responseForExchange
        );
    }

    @GetMapping("/{fromCurrency}")
    @ResponseBody
    public ResponseEntity<CoinsRate> getCurrency(@PathVariable String fromCurrency, @RequestParam Optional<List<String>> filter) {
        Optional<CoinsRate> getCurrencies;
        if (!filter.isPresent()) {
            getCurrencies = exchangeService.getAllCurrencies(fromCurrency);
        } else {
            getCurrencies = exchangeService.getFilteredCurrencies(fromCurrency, filter.get());
        }
        if (!getCurrencies.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(getCurrencies.get());
    }
}



