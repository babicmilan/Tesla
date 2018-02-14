package nl.servicehouse.billingengine.metering.domain;

import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.Convert;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Embeddable
public class Value implements Serializable {

    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    private LocalDateTime beginTimestamp;

    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    private LocalDateTime endTimestamp;

    private BigDecimal value;

    public Value() {
    }

    public Value(LocalDateTime beginTimestamp, LocalDateTime endTimestamp, BigDecimal value) {
        this. beginTimestamp = beginTimestamp;
        this.endTimestamp = endTimestamp;
        this.value = value;
    }

    public LocalDateTime getBeginTimestamp() {
        return beginTimestamp;
    }

    public void setBeginTimestamp(LocalDateTime beginTimestamp) {
        this.beginTimestamp = beginTimestamp;
    }

    public LocalDateTime getEndTimestamp() {
        return endTimestamp;
    }

    public void setEndTimestamp(LocalDateTime endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }
}
