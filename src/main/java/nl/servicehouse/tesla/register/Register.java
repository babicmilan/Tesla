package nl.servicehouse.tesla.register;

import java.math.BigDecimal;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonBackReference;

import nl.servicehouse.tesla.common.AbstractVersionedAuditable;
import nl.servicehouse.tesla.meter.Meter;
import nl.servicehouse.tesla.meter.MeteringDirection;
import nl.servicehouse.tesla.volume.UnitOfMeasure;

@Entity
@Audited
@Table(name = "metering_register")
public class Register extends AbstractVersionedAuditable {

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private TimeFrame timeFrame;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private MeteringDirection meteringDirection;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private UnitOfMeasure unitOfMeasure;

    @Column(length = 50)
    private String registerId;

    private BigDecimal multiplier;

    private Integer numberOfDigits;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "meter_id")
    @JsonBackReference
    private Meter meter;

    public TimeFrame getTimeFrame() {
        return timeFrame;
    }

    public void setTimeFrame(TimeFrame timeFrame) {
        this.timeFrame = timeFrame;
    }

    public MeteringDirection getMeteringDirection() {
        return meteringDirection;
    }

    public void setMeteringDirection(MeteringDirection meteringDirection) {
        this.meteringDirection = meteringDirection;
    }

    public UnitOfMeasure getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(UnitOfMeasure unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public String getRegisterId() {
        return registerId;
    }

    public void setRegisterId(String registerId) {
        this.registerId = registerId;
    }

    public BigDecimal getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(BigDecimal multiplier) {
        this.multiplier = multiplier;
    }

    public Integer getNumberOfDigits() {
        return numberOfDigits;
    }

    public void setNumberOfDigits(Integer numberOfDigits) {
        this.numberOfDigits = numberOfDigits;
    }

    public Meter getMeter() {
        return meter;
    }

    public void setMeter(Meter meter) {
        this.meter = meter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;
        Register register = (Register) o;
        return timeFrame == register.timeFrame && meteringDirection == register.meteringDirection && unitOfMeasure == register.unitOfMeasure && Objects.equals(
                registerId, register.registerId) && Objects.equals(multiplier, register.multiplier) && Objects.equals(numberOfDigits, register.numberOfDigits)
                && Objects.equals(meter, register.meter);
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), timeFrame, meteringDirection, unitOfMeasure, registerId, multiplier, numberOfDigits, meter);
    }
}
