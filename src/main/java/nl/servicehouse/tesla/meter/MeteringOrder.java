package nl.servicehouse.tesla.meter;

import org.joda.time.DateTime;

import nl.servicehouse.billingengine.model.AbstractPersistable;

import javax.persistence.Embeddable;
import java.util.Objects;

/**
 * Not needed first version.
 */
//@Entity
//@Table(name = "metering_meteringorder")

/**
 * Value object so it is immutable
 */

public final class MeteringOrder {

    /**
     * TODO Should be created when the order is instantiated.
     */
    private final String orderIdentifier;

    private final DateTime orderTimestamp;

    public MeteringOrder(final String orderIdentifier, final DateTime orderTimestamp) {
        this.orderIdentifier = orderIdentifier;
        this.orderTimestamp = orderTimestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MeteringOrder that = (MeteringOrder) o;
        return Objects.equals(orderIdentifier, that.orderIdentifier) &&
                Objects.equals(orderTimestamp, that.orderTimestamp);
    }

    @Override
    public int hashCode() {

        return Objects.hash(orderIdentifier, orderTimestamp);
    }

    public String getOrderIdentifier() {
        return orderIdentifier;
    }

    public DateTime getOrderTimestamp() {
        return orderTimestamp;
    }
}
