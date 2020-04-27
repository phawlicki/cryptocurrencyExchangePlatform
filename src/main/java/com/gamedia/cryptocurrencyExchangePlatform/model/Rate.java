package com.gamedia.cryptocurrencyExchangePlatform.model;

import com.fasterxml.jackson.annotation.*;

import java.io.Serializable;
import java.math.BigDecimal;

public class Rate implements Serializable {

    @JsonProperty("currency")
    private String currency;
    @JsonProperty("rate")
    private BigDecimal rate;
    @JsonProperty("amount")
    private BigDecimal amount;
    @JsonProperty("result")
    private BigDecimal result;
    @JsonProperty("fee")
    private BigDecimal fee;

    @JsonProperty("currency")
    public String getCurrency() {
        return currency;
    }

    @JsonProperty("currency")
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @JsonProperty("rate")
    public BigDecimal getRate() {
        return rate;
    }

    @JsonProperty("rate")
    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    @JsonProperty("amount")
    public BigDecimal getAmount() {
        return amount;
    }

    @JsonProperty("amount")
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @JsonProperty("result")
    public BigDecimal getResult() {
        return result;
    }

    @JsonProperty("result")
    public void setResult(BigDecimal result) {
        this.result = result;
    }

    @JsonProperty("fee")
    public BigDecimal getFee() {
        return fee;
    }

    @JsonProperty("fee")
    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }
}
