package nl.servicehouse.billingengine.metering;

import nl.servicehouse.billingengine.metering.domain.Volume;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface VolumeRepository extends PagingAndSortingRepository<Volume, Long> {

}
