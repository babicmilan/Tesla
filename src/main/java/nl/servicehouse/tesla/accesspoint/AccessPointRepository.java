package nl.servicehouse.billingengine.metering;

import nl.servicehouse.billingengine.metering.domain.AccessPoint;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccessPointRepository extends PagingAndSortingRepository<AccessPoint, Long> {

    AccessPoint findByEan(String ean);

}
