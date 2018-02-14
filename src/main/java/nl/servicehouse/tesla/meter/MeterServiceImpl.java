package nl.servicehouse.billingengine.metering;

import nl.servicehouse.billingengine.metering.domain.Meter;
import nl.servicehouse.billingengine.metering.dto.MeterDto;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class MeterServiceImpl implements MeterService {

    private MeterRepository meterRepository;

    public MeterServiceImpl(final MeterRepository meterRepository) {
        this.meterRepository = meterRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Meter> findAllMeters(Pageable pageable) {
        return meterRepository.findAll(pageable);
    }

    @Override
    public Optional<Meter> findOne(final Long id) {
        return Optional.ofNullable(meterRepository.findOne(id));
    }

    @Override
    public Meter createMeter(Meter meter) {
        return meterRepository.save(meter);
    }

    @Override
    public Meter updateMeter(final Meter meterDto) {
        return meterRepository.save(meterDto);
    }

    @Override
    public void deleteMeter(final Long id) {
        meterRepository.delete(id);
    }

}
