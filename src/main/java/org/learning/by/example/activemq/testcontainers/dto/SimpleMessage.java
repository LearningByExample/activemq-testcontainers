package org.learning.by.example.activemq.testcontainers.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;

public class SimpleMessage implements Serializable {
    private String text;
    private Date created;

    public SimpleMessage() {
        this("");
    }

    public SimpleMessage(final String text) {
        this.text = text;
        this.created = Date.from(Instant.now());
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SimpleMessage)) return false;
        SimpleMessage that = (SimpleMessage) o;
        return Objects.equals(text, that.text) &&
                Objects.equals(created, that.created);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text, created);
    }

    @Override
    public String toString() {
        return "SimpleMessage{" +
                "text='" + text + '\'' +
                ", created=" + created +
                '}';
    }
}
