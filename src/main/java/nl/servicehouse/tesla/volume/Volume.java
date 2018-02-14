package nl.servicehouse.tesla.volume;

import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Objects;

import nl.servicehouse.tesla.common.AbstractVersionedAuditable;
import nl.servicehouse.tesla.register.Register;

@Entity
@Audited
@Table(name = "metering_volume")
public class Volume extends AbstractVersionedAuditable {

    @Column(length = 20)
    private String requestIdentifier;

    @Embedded
    private Value value;

    @ManyToOne
    @JoinColumn(name = "register_id")
    private Register register;

    public String getRequestIdentifier() {
        return requestIdentifier;
    }

    public void setRequestIdentifier(String requestIdentifier) {
        this.requestIdentifier = requestIdentifier;
    }

    public Register getRegister() {
        return register;
    }

    public void setRegister(Register register) {
        this.register = register;
    }

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Volume volume = (Volume) o;
        return Objects.equals(requestIdentifier, volume.requestIdentifier) &&
                Objects.equals(value, volume.value) &&
                Objects.equals(register, volume.register);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), requestIdentifier, value, register);
    }
}
