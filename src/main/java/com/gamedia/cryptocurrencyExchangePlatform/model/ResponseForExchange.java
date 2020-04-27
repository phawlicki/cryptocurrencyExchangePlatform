package com.gamedia.cryptocurrencyExchangePlatform.model;

import com.fasterxml.jackson.annotation.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ResponseForExchange implements Serializable {

    @JsonProperty("from")
    private String from;

    @JsonProperty("to")
    private List<Rate> to = null;

    @JsonProperty("from")
    public String getFrom() {
        return from;
    }

    @JsonProperty("from")
    public void setFrom(String from) {
        this.from = from;
    }

    @JsonProperty("to")
    public List<Rate> getTo() {
        return to;
    }

    @JsonProperty("to")
    public void setTo(List<Rate> to) {
        this.to = to;
    }

}
