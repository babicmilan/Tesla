package nl.servicehouse.billingengine.metering;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import nl.servicehouse.billingengine.metering.domain.Register;

@Repository
public interface RegisterRepository extends PagingAndSortingRepository<Register, Long> {

}
