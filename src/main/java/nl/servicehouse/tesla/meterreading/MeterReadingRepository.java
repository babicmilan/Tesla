package nl.servicehouse.billingengine.metering;

import org.springframework.data.repository.PagingAndSortingRepository;

import nl.servicehouse.billingengine.metering.domain.MeterReading;

public interface MeterReadingRepository extends PagingAndSortingRepository<MeterReading, Long> {

}
