
package com.docusign.proserv.application.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "reminderEnabled",
    "reminderDelay",
    "reminderFrequency"
})
public class Reminders {

    @JsonProperty("reminderEnabled")
    private String reminderEnabled;
    @JsonProperty("reminderDelay")
    private String reminderDelay;
    @JsonProperty("reminderFrequency")
    private String reminderFrequency;

    @JsonProperty("reminderEnabled")
    public String getReminderEnabled() {
        return reminderEnabled;
    }

    @JsonProperty("reminderEnabled")
    public void setReminderEnabled(String reminderEnabled) {
        this.reminderEnabled = reminderEnabled;
    }

    @JsonProperty("reminderDelay")
    public String getReminderDelay() {
        return reminderDelay;
    }

    @JsonProperty("reminderDelay")
    public void setReminderDelay(String reminderDelay) {
        this.reminderDelay = reminderDelay;
    }

    @JsonProperty("reminderFrequency")
    public String getReminderFrequency() {
        return reminderFrequency;
    }

    @JsonProperty("reminderFrequency")
    public void setReminderFrequency(String reminderFrequency) {
        this.reminderFrequency = reminderFrequency;
    }

}
