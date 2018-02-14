package nl.servicehouse.billingengine.metering.dto;

import java.util.List;

import io.swagger.annotations.ApiModel;
import nl.servicehouse.billingengine.metering.domain.AccessPointStatus;
import nl.servicehouse.billingengine.metering.domain.Calendar;
import nl.servicehouse.billingengine.metering.domain.Commodity;

@ApiModel
public class AccessPointDto {

    private Long id;
    private String ean;
    private String description;
    private Commodity commodity;
    private AccessPointStatus status;
    private Calendar calendar;
    private String gridAreaEan;
    private Long connectionId;
    private List<MeterDto> meters;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEan() {
        return ean;
    }

    public void setEan(String ean) {
        this.ean = ean;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Commodity getCommodity() {
        return commodity;
    }

    public void setCommodity(Commodity commodity) {
        this.commodity = commodity;
    }

    public AccessPointStatus getStatus() {
        return status;
    }

    public void setStatus(AccessPointStatus status) {
        this.status = status;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public String getGridAreaEan() {
        return gridAreaEan;
    }

    public void setGridAreaEan(String gridAreaEan) {
        this.gridAreaEan = gridAreaEan;
    }

    public Long getConnectionId() {
        return connectionId;
    }

    public void setConnectionId(Long connectionId) {
        this.connectionId = connectionId;
    }

    public List<MeterDto> getMeters() {
        return meters;
    }

    public void setMeters(List<MeterDto> meters) {
        this.meters = meters;
    }
}
