package com.gamedia.cryptocurrencyExchangePlatform.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

public class CoinsRate implements Serializable {


    @JsonAlias("asset_id_base")
    private String baseCurrency;
    @JsonProperty("rates")
    private List<CurrencyRate> rates = null;

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(String baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public List<CurrencyRate> getRates() {
        return rates;
    }

    public void setRates(List<CurrencyRate> rates) {
        this.rates = rates;
    }


}
