package com.gamedia.cryptocurrencyExchangePlatform.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.math.BigDecimal;

public class CurrencyRate implements Serializable {

    @JsonProperty("asset_id_base")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String fromCurrency;
    @JsonAlias("asset_id_quote")
    private String toCurrency;
    @JsonProperty("rate")
    private BigDecimal rate;

    public CurrencyRate() {
    }

    public String getFromCurrency() {
        return fromCurrency;
    }

    public void setFromCurrency(String fromCurrency) {
        this.fromCurrency = fromCurrency;
    }

    public String getToCurrency() {
        return toCurrency;
    }

    public void setToCurrency(String toCurrency) {
        this.toCurrency = toCurrency;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }
}
