package nl.servicehouse.billingengine.metering.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import nl.servicehouse.billingengine.metering.domain.MeteringDirection;
import nl.servicehouse.billingengine.metering.domain.Register;
import nl.servicehouse.billingengine.metering.domain.TimeFrame;
import nl.servicehouse.billingengine.metering.domain.UnitOfMeasure;
import nl.servicehouse.billingengine.metering.domain.Value;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel
public class VolumeDto {

    private Long id;
    private String requestIdentifier;
    private Value value;
    private Long registerId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRequestIdentifier() {
        return requestIdentifier;
    }

    public void setRequestIdentifier(String requestIdentifier) {
        this.requestIdentifier = requestIdentifier;
    }

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
    }

    public Long getRegisterId() {
        return registerId;
    }

    public void setRegisterId(Long registerId) {
        this.registerId = registerId;
    }
}
