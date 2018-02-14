package nl.servicehouse.billingengine.metering.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.annotations.ApiModel;
import nl.servicehouse.billingengine.metering.domain.MeteringDirection;
import nl.servicehouse.billingengine.metering.domain.TimeFrame;
import nl.servicehouse.billingengine.metering.domain.UnitOfMeasure;

@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel
public class RegisterDto {

    private Long id;
    private TimeFrame timeFrame;
    private MeteringDirection meteringDirection;
    private UnitOfMeasure unitOfMeasure;
    private String registerId;
    private BigDecimal multiplier;
    private Integer numberOfDigits;
    private Long meterId;

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

    public Long getMeterId() {
        return meterId;
    }

    public void setMeterId(Long meterId) {
        this.meterId = meterId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
