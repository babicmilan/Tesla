package nl.servicehouse.billingengine.metering.domain;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import org.hibernate.envers.Audited;


@Entity
@Audited
@Table(name = "metering_accesspoint")
public class AccessPoint extends AbstractVersionedAuditable {

    @NotNull(message = "Required argument ean cannot be null!")
    private String ean;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Commodity commodity;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private AccessPointStatus status;

    //TODO what about profileCategory
    //private ProfileCategory profileCategory;

    @Enumerated(EnumType.STRING)
    private Calendar calendar;

    private String gridAreaEan;

    @Column(nullable = false, unique = true)
    private Long connectionId;

    @OneToMany(cascade = { CascadeType.ALL }, orphanRemoval = true, mappedBy = "accessPoint")
    @JsonManagedReference
    private List<Meter> meters;

    public AccessPoint() {
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

    public List<Meter> getMeters() {
        return meters;
    }

    public void setMeters(List<Meter> meters) {
        this.meters = meters;
    }


}
