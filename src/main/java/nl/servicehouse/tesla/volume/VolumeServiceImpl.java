package nl.servicehouse.tesla.volume;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
@Service
@Transactional
public class VolumeServiceImpl implements VolumeService {

    private final VolumeRepository volumeRepository;

    public VolumeServiceImpl(final VolumeRepository volumeRepository) {
        this.volumeRepository = volumeRepository;
    }

    @Override
    public Page<Volume> getAllVolumes(Pageable pageable) {
        return volumeRepository.findAll(pageable);
    }

    @Override
    public Optional<Volume> getVolumeById(Long id) {
        return Optional.ofNullable(volumeRepository.findOne(id));
    }

    @Override
    public Volume createVolume(Volume volume) {
        return volumeRepository.save(volume);
    }

    @Override
    public void deleteVolume(Long id) {
        volumeRepository.delete(id);
    }

    @Override
    public Volume updateVolume(Volume volume) {
        return volumeRepository.save(volume);
    }
}
