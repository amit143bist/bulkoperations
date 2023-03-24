
package com.docusign.proserv.application.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "expireEnabled",
    "expireAfter",
    "expireWarn"
})
public class Expirations {

    @JsonProperty("expireEnabled")
    private String expireEnabled;
    @JsonProperty("expireAfter")
    private String expireAfter;
    @JsonProperty("expireWarn")
    private String expireWarn;

    @JsonProperty("expireEnabled")
    public String getExpireEnabled() {
        return expireEnabled;
    }

    @JsonProperty("expireEnabled")
    public void setExpireEnabled(String expireEnabled) {
        this.expireEnabled = expireEnabled;
    }

    @JsonProperty("expireAfter")
    public String getExpireAfter() {
        return expireAfter;
    }

    @JsonProperty("expireAfter")
    public void setExpireAfter(String expireAfter) {
        this.expireAfter = expireAfter;
    }

    @JsonProperty("expireWarn")
    public String getExpireWarn() {
        return expireWarn;
    }

    @JsonProperty("expireWarn")
    public void setExpireWarn(String expireWarn) {
        this.expireWarn = expireWarn;
    }

}
