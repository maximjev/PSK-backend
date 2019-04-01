package com.psk.backend.common;

import com.querydsl.core.annotations.QueryEmbeddable;
import lombok.ToString;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;
import org.pojomatic.annotations.AutoProperty;

import static org.codehaus.jackson.annotate.JsonAutoDetect.Visibility.NONE;

@QueryEmbeddable
@AutoProperty
@JsonAutoDetect(getterVisibility = NONE, fieldVisibility = NONE, isGetterVisibility = NONE, setterVisibility = NONE)
@ToString
public class ErrorItem extends Exception {

    private Error reason;
    private String description;

    public ErrorItem() {
    }

    public ErrorItem(Error reason, Exception ex) {
        super(ex.getMessage());
        this.reason = reason;
        this.description = ex.getMessage();
    }

    public ErrorItem(Error reason, String description) {
        super(description);
        this.reason = reason;
        this.description = description;
    }

    public ErrorItem(Error error) {
        this.reason = error;
        this.description = null;
    }

    @JsonProperty
    public Error getReason() {
        return reason;
    }

    @JsonProperty
    public String getDescription() {
        return description;
    }
}
