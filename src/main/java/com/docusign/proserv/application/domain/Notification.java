
package com.docusign.proserv.application.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "reminders",
    "expirations"
})
public class Notification {

    @JsonProperty("reminders")
    private Reminders reminders;
    @JsonProperty("expirations")
    private Expirations expirations;

    @JsonProperty("reminders")
    public Reminders getReminders() {
        return reminders;
    }

    @JsonProperty("reminders")
    public void setReminders(Reminders reminders) {
        this.reminders = reminders;
    }

    @JsonProperty("expirations")
    public Expirations getExpirations() {
        return expirations;
    }

    @JsonProperty("expirations")
    public void setExpirations(Expirations expirations) {
        this.expirations = expirations;
    }

}
