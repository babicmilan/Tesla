package nl.servicehouse.billingengine.metering.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Audited
@Table(name = "metering_meter")
public class Meter extends AbstractVersionedAuditable {

    @NotNull(message = "Required argument meter number cannot be null!")
    @Column(unique = true)
    private String meterNumber;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private MeterStatus status;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private MeterType meterType;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private MeteringType meteringType;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private MeterReadingFrequency meterReadingFrequency;

    private int buildYear;

    @NotNull
    @Convert(converter = Jsr310JpaConverters.LocalDateConverter.class)
    private LocalDate activationDate;

    @Convert(converter = Jsr310JpaConverters.LocalDateConverter.class)
    private LocalDate deactivationDate;

    @OneToMany(cascade = { CascadeType.ALL }, orphanRemoval = true, mappedBy = "meter")
    private List<Register> register = new ArrayList<>();

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "accessPoint_id")
    @JsonBackReference
    private AccessPoint accessPoint;

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

    public List<Register> getRegister() {
        return register;
    }

    public AccessPoint getAccessPoint() {
        return accessPoint;
    }

    public void setAccessPoint(AccessPoint accessPoint) {
        this.accessPoint = accessPoint;
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

    public void setRegister(List<Register> registers) {
        //TODO should we clear or add register
        // this.register.clear();
        if (registers != null)
            this.register.addAll(registers);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Meter meter = (Meter) o;
        return buildYear == meter.buildYear &&
                Objects.equals(meterNumber, meter.meterNumber) &&
                Objects.equals(description, meter.description) &&
                status == meter.status &&
                meterType == meter.meterType &&
                meteringType == meter.meteringType &&
                meterReadingFrequency == meter.meterReadingFrequency &&
                Objects.equals(activationDate, meter.activationDate) &&
                Objects.equals(deactivationDate, meter.deactivationDate) &&
                Objects.equals(register, meter.register) &&
                Objects.equals(accessPoint, meter.accessPoint);
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), meterNumber, description, status, meterType, meteringType, meterReadingFrequency, buildYear, activationDate, deactivationDate, register, accessPoint);
    }
}
