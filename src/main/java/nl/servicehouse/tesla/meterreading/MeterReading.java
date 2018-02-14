package nl.servicehouse.tesla.meterreading;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.data.jpa.domain.AbstractPersistable;

import com.fasterxml.jackson.annotation.JsonBackReference;

import nl.servicehouse.tesla.register.Register;

@Entity(name = "teslaMeterReading")
@Table(name = "metering_meterreading")
public class MeterReading extends AbstractPersistable {

    /**
     * Soft reference to the order the meterReading is a result of.
     */
    @NotNull
    private String orderIdentifier;

    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    private LocalDateTime requestTimestamp;

    /**
     * In the case we only get day-values, they are stored as midnight.
     */
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    private LocalDateTime readingTimestamp;

    @NotNull
    private BigDecimal value;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private TypeOfOrigin typeOfOrigin;

    @ManyToOne
    @JoinColumn(name = "register_id")
    @JsonBackReference
    private Register register;

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

    public Register getRegister() {
        return register;
    }

    public void setRegister(Register register) {
        this.register = register;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;
        MeterReading that = (MeterReading) o;
        return Objects.equals(orderIdentifier, that.orderIdentifier) && Objects.equals(requestTimestamp, that.requestTimestamp) && Objects.equals(
                readingTimestamp, that.readingTimestamp) && Objects.equals(value, that.value) && typeOfOrigin == that.typeOfOrigin && Objects.equals(register,
                that.register);
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), orderIdentifier, requestTimestamp, readingTimestamp, value, typeOfOrigin, register);
    }
}
