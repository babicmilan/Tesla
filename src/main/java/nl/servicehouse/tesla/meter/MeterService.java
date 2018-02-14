package nl.servicehouse.tesla.meter;

import nl.servicehouse.billingengine.metering.domain.Meter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface MeterService {
    Page<Meter> findAllMeters(Pageable pageable);

    Optional<Meter> findOne(Long id);

    Meter createMeter(Meter meter);

    Meter updateMeter(Meter meter);

    void deleteMeter(Long id);
}
