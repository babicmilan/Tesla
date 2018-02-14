package nl.servicehouse.billingengine.metering;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nl.servicehouse.billingengine.metering.domain.MeterReading;
import nl.servicehouse.billingengine.metering.dto.MeterReadingDto;

@Service
@Transactional
public class MeterReadingServiceImpl implements MeterReadingService {

    private final MeterReadingRepository meterReadingRepository;

    @Autowired
    public MeterReadingServiceImpl(MeterReadingRepository meterReadingRepository) {
        this.meterReadingRepository = meterReadingRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MeterReading> getAllMeterReadings(Pageable pageable) {
        return meterReadingRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MeterReading> getMeterReadingById(Long id) {
        return Optional.ofNullable(meterReadingRepository.findOne(id));
    }

    @Override
    public MeterReading createMeterReading(MeterReading meterReading) {
        return meterReadingRepository.save(meterReading);
    }

    @Override
    public void deleteMeterReading(Long id) {
        meterReadingRepository.delete(id);
    }

    @Override
    public MeterReading updateMeterReading(MeterReading meterReading) {
        return meterReadingRepository.save(meterReading);
    }


}
