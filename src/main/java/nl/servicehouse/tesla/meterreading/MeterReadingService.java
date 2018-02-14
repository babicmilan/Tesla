package nl.servicehouse.billingengine.metering;

import nl.servicehouse.billingengine.metering.domain.MeterReading;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface MeterReadingService {

    Page<MeterReading> getAllMeterReadings(Pageable pageable);

    Optional<MeterReading> getMeterReadingById(Long id);

    MeterReading createMeterReading(MeterReading meterReading);

    void deleteMeterReading(Long id);

    MeterReading updateMeterReading(MeterReading meterReading);

}
