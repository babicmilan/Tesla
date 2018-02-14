package nl.servicehouse.tesla.meter;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MeterRepository extends PagingAndSortingRepository<Meter, Long> {
}
