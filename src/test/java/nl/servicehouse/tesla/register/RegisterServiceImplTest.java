package nl.servicehouse.tesla.register;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import nl.servicehouse.billingengine.metering.domain.Meter;
import nl.servicehouse.billingengine.metering.domain.MeteringDirection;
import nl.servicehouse.billingengine.metering.domain.Register;
import nl.servicehouse.billingengine.metering.domain.TimeFrame;
import nl.servicehouse.billingengine.metering.domain.UnitOfMeasure;
import nl.servicehouse.tesla.meter.MeterRepository;

@RunWith(MockitoJUnitRunner.class)
public class RegisterServiceImplTest {

    private RegisterServiceImpl registerService;

    @Mock
    private RegisterRepository registerRepositoryMock;
    @Mock
    private MeterRepository meterRepositoryMock;
    private ModelMapper modelMapper = new ModelMapper();

    @Before
    public void setUp() throws Exception {
        registerService = new RegisterServiceImpl(registerRepositoryMock, meterRepositoryMock, modelMapper);
    }

    @Test
    public void findAll() {
        when(registerRepositoryMock.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(Arrays.asList(getRegisterForTest(1L), getRegisterForTest(2L))));

        Page<Register> registers = registerService.getAllRegisters(new PageRequest(0, 10));

        assertThat(registers.getTotalElements()).isEqualTo(2);
    }

    @Test
    public void findOne() {
        when(registerRepositoryMock.findOne(anyLong())).thenReturn(getRegisterForTest(1L));

        Optional<Register> result = registerService.getRegisterById(1L);

        if (result.isPresent()) {
            Register register = result.get();
            assertThat(register.getId()).isEqualTo(1L);
            assertThat(register.getMeter().getId()).isEqualTo(1L);
            assertThat(register.getTimeFrame()).isEqualTo(TimeFrame.OFF_PEAK);
            assertThat(register.getMeteringDirection()).isEqualTo(MeteringDirection.CONSUMPTION);
            assertThat(register.getMultiplier()).isEqualTo(new BigDecimal(10));
            assertThat(register.getNumberOfDigits()).isEqualTo(3);
            assertThat(register.getRegisterId()).isEqualTo("123");
            assertThat(register.getUnitOfMeasure()).isEqualTo(UnitOfMeasure.KWH);
        }
    }

    @Test
    public void createRegister() {
        Meter meter = new Meter();
        meter.setId(1L);
        when(meterRepositoryMock.findOne(anyLong())).thenReturn(meter);
        when(registerRepositoryMock.save(any(Register.class))).thenReturn(getRegisterForTest(1L));

        Register register = registerService.createRegister(getRegisterForTest(1L));

        assertThat(register.getId()).isEqualTo(1L);
        assertThat(register.getMeter().getId()).isEqualTo(1L);
        assertThat(register.getTimeFrame()).isEqualTo(TimeFrame.OFF_PEAK);
        assertThat(register.getMeteringDirection()).isEqualTo(MeteringDirection.CONSUMPTION);
        assertThat(register.getMultiplier()).isEqualTo(new BigDecimal(10));
        assertThat(register.getNumberOfDigits()).isEqualTo(3);
        assertThat(register.getRegisterId()).isEqualTo("123");
        assertThat(register.getUnitOfMeasure()).isEqualTo(UnitOfMeasure.KWH);
    }

    @Test
    public void updateRegister() {
        Register registerForTest = getRegisterForTest(1L);
        when(registerRepositoryMock.findOne(anyLong())).thenReturn(registerForTest);
        Meter meter = new Meter();
        meter.setId(1L);
        when(meterRepositoryMock.findOne(anyLong())).thenReturn(meter);
        when(registerRepositoryMock.save(any(Register.class))).thenReturn(registerForTest);

        Register register = registerService.updateRegister(getRegisterForTest(1L));

        assertThat(register.getId()).isEqualTo(1L);
        assertThat(register.getMeter().getId()).isEqualTo(1L);
        assertThat(register.getTimeFrame()).isEqualTo(TimeFrame.OFF_PEAK);
        assertThat(register.getMeteringDirection()).isEqualTo(MeteringDirection.CONSUMPTION);
        assertThat(register.getMultiplier()).isEqualTo(new BigDecimal(10));
        assertThat(register.getNumberOfDigits()).isEqualTo(3);
        assertThat(register.getRegisterId()).isEqualTo("123");
        assertThat(register.getUnitOfMeasure()).isEqualTo(UnitOfMeasure.KWH);
    }

    @Test
    public void deleteRegister() {
        registerService.deleteRegister(1L);

        Mockito.verify(registerRepositoryMock)
                .delete(1L);
    }

    private Register getRegisterForTest(Long id) {
        Register register = new Register();
        register.setId(id);
        register.setTimeFrame(TimeFrame.OFF_PEAK);
        register.setMeteringDirection(MeteringDirection.CONSUMPTION);
        register.setMultiplier(new BigDecimal(10));
        register.setNumberOfDigits(3);
        register.setRegisterId("123");
        register.setUnitOfMeasure(UnitOfMeasure.KWH);

        Meter meter = new Meter();
        meter.setId(1L);
        register.setMeter(meter);

        return register;
    }

}