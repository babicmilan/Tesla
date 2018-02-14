package nl.servicehouse.billingengine.metering.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

@MappedSuperclass
public class AbstractPersistable extends org.springframework.data.jpa.domain.AbstractPersistable<Long> {

    @Override
    public void setId(Long id) {
        if (getId() != null) {
            throw new IllegalStateException("Cannot change the id of a JPA managed entity");
        }
        super.setId(id);
    }

    /**
     * Overridden so that we can add the {@link @JsonIgnore} annotation.
     *
     * @see org.springframework.data.domain.Persistable#isNew()
     */
    @Override
    @JsonIgnore
    @Transient
    public boolean isNew() {
        return null == getId();
    }

}
