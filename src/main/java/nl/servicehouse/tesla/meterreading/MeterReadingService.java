package nl.servicehouse.tesla.meterreading;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MeterReadingService {

    Page<MeterReading> getAllMeterReadings(Pageable pageable);

    Optional<MeterReading> getMeterReadingById(Long id);

    MeterReading createMeterReading(MeterReading meterReading);

    void deleteMeterReading(Long id);

    MeterReading updateMeterReading(MeterReading meterReading);

}
