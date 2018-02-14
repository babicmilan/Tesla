package nl.servicehouse.tesla.meter;

import nl.servicehouse.billingengine.metering.domain.AccessPoint;
import nl.servicehouse.billingengine.metering.domain.Meter;
import nl.servicehouse.billingengine.metering.domain.MeterReadingFrequency;
import nl.servicehouse.billingengine.metering.domain.MeterStatus;
import nl.servicehouse.billingengine.metering.domain.MeterType;
import nl.servicehouse.billingengine.metering.domain.MeteringType;
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

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MeterServiceImplTest {

    private MeterServiceImpl meterService;

    @Mock
    private MeterRepository meterRepositoryMock;

    @Before
    public void setUp() throws Exception {
        meterService = new MeterServiceImpl(meterRepositoryMock);
    }

    @Test
    public void findAll() {
        when(meterRepositoryMock.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(Arrays.asList(getMeterForTest(1L), getMeterForTest(2L))));

        Page<Meter> meters = meterService.findAllMeters(new PageRequest(0, 10));

        assertThat(meters.getTotalElements()).isEqualTo(2);
    }

    @Test
    public void findOne() {
        when(meterRepositoryMock.findOne(anyLong())).thenReturn(getMeterForTest(1L));

        Optional<Meter> result = meterService.findOne(1L);

        if (result.isPresent()) {
            Meter meter = result.get();
            assertThat(meter.getId()).isEqualTo(1L);
            assertThat(meter.getAccessPoint().getId()).isEqualTo(1L);
            assertThat(meter.getActivationDate()).isEqualTo(LocalDate.now());
            assertThat(meter.getDeactivationDate()).isEqualTo(LocalDate.now().plusYears(2));
            assertThat(meter.getBuildYear()).isEqualTo(2018);
            assertThat(meter.getDescription()).isEqualTo("Description");
            assertThat(meter.getMeterNumber()).isEqualTo("123");
            assertThat(meter.getMeteringType()).isEqualTo(MeteringType.MANUAL);
            assertThat(meter.getMeterReadingFrequency()).isEqualTo(MeterReadingFrequency.YEARLY);
            assertThat(meter.getMeterType()).isEqualTo(MeterType.INTERVAL);
        }
    }

    @Test
    public void createMeter() {
        AccessPoint accessPoint = new AccessPoint();
        accessPoint.setId(1L);
        when(meterRepositoryMock.save(any(Meter.class))).thenReturn(getMeterForTest(1L));

        Meter meter = meterService.createMeter(createMeterForTest(1L));

        assertThat(meter.getId()).isEqualTo(1L);
        assertThat(meter.getAccessPoint().getId()).isEqualTo(1L);
        assertThat(meter.getActivationDate()).isEqualTo(LocalDate.now());
        assertThat(meter.getDeactivationDate()).isEqualTo(LocalDate.now().plusYears(2));
        assertThat(meter.getBuildYear()).isEqualTo(2018);
        assertThat(meter.getDescription()).isEqualTo("Description");
        assertThat(meter.getMeterNumber()).isEqualTo("123");
        assertThat(meter.getMeteringType()).isEqualTo(MeteringType.MANUAL);
        assertThat(meter.getMeterReadingFrequency()).isEqualTo(MeterReadingFrequency.YEARLY);
        assertThat(meter.getMeterType()).isEqualTo(MeterType.INTERVAL);

    }

    @Test
    public void updateMeter() {
        Meter meterForTest = getMeterForTest(1L);
        when(meterRepositoryMock.findOne(anyLong())).thenReturn(meterForTest);
        AccessPoint accessPoint = new AccessPoint();
        accessPoint.setId(1L);
        when(meterRepositoryMock.save(any(Meter.class))).thenReturn(meterForTest);

        Meter meter = meterService.updateMeter(createMeterForTest(1L));

        assertThat(meter.getId()).isEqualTo(1L);
        assertThat(meter.getAccessPoint().getId()).isEqualTo(1L);
        assertThat(meter.getActivationDate()).isEqualTo(LocalDate.now());
        assertThat(meter.getDeactivationDate()).isEqualTo(LocalDate.now().plusYears(2));
        assertThat(meter.getBuildYear()).isEqualTo(2018);
        assertThat(meter.getDescription()).isEqualTo("Description");
        assertThat(meter.getMeterNumber()).isEqualTo("123");
        assertThat(meter.getMeteringType()).isEqualTo(MeteringType.MANUAL);
        assertThat(meter.getMeterReadingFrequency()).isEqualTo(MeterReadingFrequency.YEARLY);
        assertThat(meter.getMeterType()).isEqualTo(MeterType.INTERVAL);
    }

    @Test
    public void deleteMeter() {
        meterService.deleteMeter(1L);

        Mockito.verify(meterRepositoryMock)
                .delete(1L);
    }

    private Meter getMeterForTest(Long id) {
        Meter meter = new Meter();
        meter.setId(id);
        meter.setMeterNumber("123");
        meter.setActivationDate(LocalDate.now());
        meter.setDeactivationDate(LocalDate.now().plusYears(2));
        meter.setBuildYear(2018);
        meter.setMeteringType(MeteringType.MANUAL);
        meter.setDescription("Description");
        meter.setMeterReadingFrequency(MeterReadingFrequency.YEARLY);
        meter.setStatus(MeterStatus.ACTIVE);
        meter.setMeterType(MeterType.INTERVAL);

        AccessPoint accessPoint = new AccessPoint();
        accessPoint.setId(1L);
        meter.setAccessPoint(accessPoint);

        return meter;
    }

    private Meter createMeterForTest(Long id) {
        Meter meter = new Meter();
        meter.setId(id);
        meter.setMeterNumber("12345");
        AccessPoint accessPoint = new AccessPoint();
        accessPoint.setId(1L);
        meter.setAccessPoint(accessPoint);
        meter.setActivationDate(LocalDate.now());
        meter.setDeactivationDate(LocalDate.now().plusYears(2));
        meter.setBuildYear(2018);
        meter.setMeteringType(MeteringType.MANUAL);
        meter.setDescription("Description");
        meter.setMeterReadingFrequency(MeterReadingFrequency.YEARLY);
        meter.setStatus(MeterStatus.ACTIVE);
        return meter;
    }
}