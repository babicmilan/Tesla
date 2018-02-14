package nl.servicehouse.billingengine.metering.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import io.swagger.annotations.ApiModel;
import nl.servicehouse.billingengine.metering.domain.MeterReadingFrequency;
import nl.servicehouse.billingengine.metering.domain.MeterStatus;
import nl.servicehouse.billingengine.metering.domain.MeterType;
import nl.servicehouse.billingengine.metering.domain.MeteringType;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Convert;
import java.time.LocalDate;
import java.util.List;

import static nl.servicehouse.billingengine.metering.MeteringConstants.DATE_PATTERN;

@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel
public class MeterDto {

    private Long id;
    private String meterNumber;
    private String description;
    private MeterStatus status;
    private MeterType meterType;
    private MeteringType meteringType;
    private MeterReadingFrequency meterReadingFrequency;
    private int buildYear;
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate activationDate;
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate deactivationDate;
    private Long accessPointId;
    private List<RegisterDto> register;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMeterNumber() {
        return meterNumber;
    }

    public String getDescription() {
        return description;
    }

    public MeterStatus getStatus() {
        return status;
    }

    public MeterType getMeterType() {
        return meterType;
    }

    public MeteringType getMeteringType() {
        return meteringType;
    }

    public MeterReadingFrequency getMeterReadingFrequency() {
        return meterReadingFrequency;
    }

    public int getBuildYear() {
        return buildYear;
    }

    public LocalDate getActivationDate() {
        return activationDate;
    }

    public LocalDate getDeactivationDate() {
        return deactivationDate;
    }

    public Long getAccessPointId() {
        return accessPointId;
    }

    public void setAccessPointId(Long accessPointId) {
        this.accessPointId = accessPointId;
    }

    public List<RegisterDto> getRegister() {
        return register;
    }

    public void setRegister(List<RegisterDto> register) {
        this.register = register;
    }

    public void setMeterNumber(String meterNumber) {
        this.meterNumber = meterNumber;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(MeterStatus status) {
        this.status = status;
    }

    public void setMeterType(MeterType meterType) {
        this.meterType = meterType;
    }

    public void setMeteringType(MeteringType meteringType) {
        this.meteringType = meteringType;
    }

    public void setMeterReadingFrequency(MeterReadingFrequency meterReadingFrequency) {
        this.meterReadingFrequency = meterReadingFrequency;
    }

    public void setBuildYear(int buildYear) {
        this.buildYear = buildYear;
    }

    public void setActivationDate(LocalDate activationDate) {
        this.activationDate = activationDate;
    }

    public void setDeactivationDate(LocalDate deactivationDate) {
        this.deactivationDate = deactivationDate;
    }
}
