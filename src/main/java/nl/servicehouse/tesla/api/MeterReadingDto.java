package nl.servicehouse.tesla.api;

import static nl.servicehouse.tesla.common.MeteringConstants.DATE_PATTERN;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;

import nl.servicehouse.tesla.meterreading.TypeOfOrigin;
import org.springframework.format.annotation.DateTimeFormat;


@ApiModel
public class MeterReadingDto {

    private Long id;
    private String orderIdentifier;
    @DateTimeFormat(pattern = DATE_PATTERN)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime requestTimestamp;
    @DateTimeFormat(pattern = DATE_PATTERN)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime readingTimestamp;
    private BigDecimal value;
    private TypeOfOrigin typeOfOrigin;
    private Long registerId;

    public MeterReadingDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderIdentifier() {
        return orderIdentifier;
    }

    public void setOrderIdentifier(String orderIdentifier) {
        this.orderIdentifier = orderIdentifier;
    }

    public LocalDateTime getRequestTimestamp() {
        return requestTimestamp;
    }

    public void setRequestTimestamp(LocalDateTime requestTimestamp) {
        this.requestTimestamp = requestTimestamp;
    }

    public LocalDateTime getReadingTimestamp() {
        return readingTimestamp;
    }

    public void setReadingTimestamp(LocalDateTime readingTimestamp) {
        this.readingTimestamp = readingTimestamp;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public TypeOfOrigin getTypeOfOrigin() {
        return typeOfOrigin;
    }

    public void setTypeOfOrigin(TypeOfOrigin typeOfOrigin) {
        this.typeOfOrigin = typeOfOrigin;
    }

    public Long getRegisterId() {
        return registerId;
    }

    public void setRegisterId(Long registerId) {
        this.registerId = registerId;
    }
}
