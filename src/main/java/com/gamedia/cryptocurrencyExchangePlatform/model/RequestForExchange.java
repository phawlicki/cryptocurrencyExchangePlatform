package com.gamedia.cryptocurrencyExchangePlatform.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class RequestForExchange {
    public RequestForExchange() {
    }

    private String from;
    private List<String> to = new ArrayList<>();
    private BigDecimal amount;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public List<String> getTo() {
        return to;
    }

    public void setTo(List<String> to) {
        this.to = to;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
