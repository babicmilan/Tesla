package nl.servicehouse.billingengine.metering;

import nl.servicehouse.billingengine.metering.domain.Volume;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface VolumeService {

    Page<Volume> getAllVolumes(Pageable pageable);

    Optional<Volume> getVolumeById(Long id);

    Volume createVolume(Volume volume);

    void deleteVolume(Long id);

    Volume updateVolume(Volume volume);

}
