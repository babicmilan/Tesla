package nl.servicehouse.tesla.meterreading;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import nl.servicehouse.billingengine.metering.domain.Register;

@RunWith(MockitoJUnitRunner.class)
public class MeterReadingServiceImpTest {

    private MeterReadingServiceImpl meterReadingService;

    @Mock
    private MeterReadingRepository meterReadingRepository;

    @Mock
    private RegisterRepository registerRepository;

    @Before
    public void before() {
        meterReadingService = new MeterReadingServiceImpl(meterReadingRepository);
    }

    @Test
    public void getAllMeterReadings() {
        when(meterReadingRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(Arrays.asList(createMeterReading(1L), createMeterReading(2L))));

        Page<MeterReading> meterReadings = meterReadingService.getAllMeterReadings(new PageRequest(0, 10));

        assertThat(meterReadings.getTotalElements()).isEqualTo(2);
    }

    @Test
    public void getMeterReadingById() {
        when(meterReadingRepository.findOne(any(Long.class))).thenReturn(createMeterReading(1L));

        Optional<MeterReading> result = meterReadingService.getMeterReadingById(1L);

        assertThat(result.isPresent()).isTrue();
        if (result.isPresent()) {
            MeterReading meterReading = result.get();
            assertThat(meterReading.getId()).isEqualTo(1L);
            assertThat(meterReading.getOrderIdentifier()).isEqualTo("order");
            assertThat(meterReading.getReadingTimestamp()).isEqualTo(LocalDateTime.of(2,2,2,2,2));
            assertThat(meterReading.getRequestTimestamp()).isEqualTo(LocalDateTime.of(2,2,2,2,2));
            assertThat(meterReading.getValue()).isEqualTo(new BigDecimal(100));
            assertThat(meterReading.getRegister().getId()).isEqualTo(1L);
        }
    }

    @Test
    public void createMeterReading() {
        when(meterReadingRepository.save(any(MeterReading.class))).thenReturn(createMeterReading(1L));
        Register register = new Register();
        register.setId(1L);
        when(registerRepository.findOne(anyLong())).thenReturn(register);

        MeterReading meterReading = meterReadingService.createMeterReading(createMeterReading(1L));

        assertThat(meterReading.getId()).isEqualTo(1L);
        assertThat(meterReading.getOrderIdentifier()).isEqualTo("order");
        assertThat(meterReading.getReadingTimestamp()).isEqualTo(LocalDateTime.of(2,2,2,2,2));
        assertThat(meterReading.getRequestTimestamp()).isEqualTo(LocalDateTime.of(2,2,2,2,2));
        assertThat(meterReading.getValue()).isEqualTo(new BigDecimal(100));
        assertThat(meterReading.getRegister().getId()).isEqualTo(1L);
    }

    @Test
    public void updateMeterReading() {
        MeterReading mr = createMeterReading(1L);
        when(meterReadingRepository.save(any(MeterReading.class))).thenReturn(mr);
        when(meterReadingRepository.findOne(any(Long.class))).thenReturn(mr);
        Register register = new Register();
        register.setId(1L);
        when(registerRepository.findOne(anyLong())).thenReturn(register);

        MeterReading meterReading = meterReadingService.createMeterReading(createMeterReading(1L));

        assertThat(meterReading.getId()).isEqualTo(1L);
        assertThat(meterReading.getOrderIdentifier()).isEqualTo("order");
        assertThat(meterReading.getReadingTimestamp()).isEqualTo(LocalDateTime.of(2,2,2,2,2));
        assertThat(meterReading.getRequestTimestamp()).isEqualTo(LocalDateTime.of(2,2,2,2,2));
        assertThat(meterReading.getValue()).isEqualTo(new BigDecimal(100));
        assertThat(meterReading.getRegister().getId()).isEqualTo(1L);
    }

    @Test
    public void deleteMeterReading() {
        meterReadingService.deleteMeterReading(1L);

        Mockito.verify(meterReadingRepository)
                .delete(1L);
    }

    private MeterReading createMeterReading(Long id) {
        MeterReading meterReading = new MeterReading();
        meterReading.setId(id);
        meterReading.setOrderIdentifier("order");
        meterReading.setReadingTimestamp(LocalDateTime.of(2,2,2,2,2));
        meterReading.setRequestTimestamp(LocalDateTime.of(2,2,2,2,2));
        meterReading.setValue(new BigDecimal(100));
        Register register = new Register();
        register.setId(1L);
        meterReading.setRegister(register);
        return meterReading;
    }

}