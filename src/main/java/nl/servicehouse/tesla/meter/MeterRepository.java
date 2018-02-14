package nl.servicehouse.billingengine.metering;

import nl.servicehouse.billingengine.metering.domain.Meter;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface MeterRepository extends PagingAndSortingRepository<Meter, Long> {
}
