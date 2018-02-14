package nl.servicehouse.tesla.common;

import org.hibernate.envers.Audited;
import org.joda.time.DateTime;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

/**
 * Parent class for entities with a version for optimistic locking purposes and
 * fields that indicate when and by whom the last modification to an entity was made.
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Audited
public abstract class AbstractVersionedAuditable extends AbstractPersistable {

    @Version
    private Long version;

    @LastModifiedBy
    @Column(nullable = false)
    private String lastMutatedBy;

    @LastModifiedDate
    @Column(nullable = false)
    private DateTime lastMutatedDate;

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getLastMutatedBy() {
        return lastMutatedBy;
    }

    public DateTime getLastMutatedDate() {
        return lastMutatedDate;
    }


}
