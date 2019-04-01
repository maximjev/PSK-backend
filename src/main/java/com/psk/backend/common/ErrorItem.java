package com.psk.backend.common;

import com.querydsl.core.annotations.QueryEmbeddable;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

@QueryEmbeddable
@EqualsAndHashCode
@ToString
public class ErrorItem {
    private static final Logger LOG = getLogger(ErrorItem.class);

    private Error reason;
    private String description;

    public ErrorItem() {
    }

    public ErrorItem(Error reason, Exception ex) {
        this.reason = reason;
        this.description = ex.getMessage();
        LOG.debug(ex.getMessage(), ex);
    }

    public ErrorItem(Error reason, String description) {
        this.reason = reason;
        this.description = description;
    }

    public ErrorItem(Error error) {
        this.reason = error;
        this.description = null;
    }

    public Error getReason() {
        return reason;
    }

    public String getDescription() {
        return description;
    }
}
