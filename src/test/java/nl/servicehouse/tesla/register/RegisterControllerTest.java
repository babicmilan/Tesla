package nl.servicehouse.tesla.register;

import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.servicehouse.billingengine.api.pub.registration.DatabindingControllerAdvice;
import nl.servicehouse.billingengine.api.pub.registration.ExceptionControllerAdvice;
import nl.servicehouse.billingengine.metering.domain.Meter;
import nl.servicehouse.billingengine.metering.domain.MeteringDirection;
import nl.servicehouse.billingengine.metering.domain.Register;
import nl.servicehouse.billingengine.metering.domain.TimeFrame;
import nl.servicehouse.billingengine.metering.domain.UnitOfMeasure;
import nl.servicehouse.tesla.common.MeteringConstants;
import nl.servicehouse.tesla.meter.MeterService;

@RunWith(MockitoJUnitRunner.class)
public class RegisterControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private RegisterService registerServiceMock;

    @Mock
    private MeterService meterServiceMock;

    private ModelMapper modelMapper = new ModelMapper();

    @Before
    public void setUp() throws Exception {

        this.mockMvc = MockMvcBuilders.standaloneSetup(new RegisterController(registerServiceMock, meterServiceMock, modelMapper))
                .setControllerAdvice(new DatabindingControllerAdvice(), new ExceptionControllerAdvice())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();

        this.objectMapper = new ObjectMapper();
    }

    @Test
    public void getAllRegisters() throws Exception {
        given(registerServiceMock.getAllRegisters(any(Pageable.class))).willReturn(
                new PageImpl<>(Arrays.asList(getRegisterForTest(1L), getRegisterForTest(2L))));

        this.mockMvc.perform(get(MeteringConstants.METERING_BASE_PATH + "/registers").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id", is(1)))
                .andExpect(jsonPath("$.content[1].id", is(2)));
    }

    @Test
    public void getOne() throws Exception {
        given(registerServiceMock.getRegisterById(anyLong())).willReturn(Optional.of(getRegisterForTest(1L)));

        this.mockMvc.perform(get(MeteringConstants.METERING_BASE_PATH + "/registers/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.timeFrame", is("OFF_PEAK")))
                .andExpect(jsonPath("$.meteringDirection", is("CONSUMPTION")))
                .andExpect(jsonPath("$.meterId", is(1)))
                .andExpect(jsonPath("$.multiplier", is(10)))
                .andExpect(jsonPath("$.registerId", is("123")))
                .andExpect(jsonPath("$.unitOfMeasure", is("KWH")))
                .andExpect(jsonPath("$.numberOfDigits", is(3)));
    }

    @Test
    public void createRegister() throws Exception {
        Register register = new Register();
        register.setId(10L);
        given(registerServiceMock.getRegisterById(anyLong())).willReturn(Optional.of(getRegisterForTest(1L)));
        given(meterServiceMock.findOne(anyLong())).willReturn(Optional.ofNullable(new Meter()));
        given(registerServiceMock.createRegister(any(Register.class))).willReturn(register);

        this.mockMvc.perform(post(MeteringConstants.METERING_BASE_PATH + "/registers").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(getRegisterForTest(1L))))
                .andExpect(status().isCreated())
                .andExpect(header().stringValues("Location", "http://localhost" + MeteringConstants.METERING_BASE_PATH + "/registers/10"));
    }

    @Test
    public void updateRegister() throws Exception {
        Register registerForTest = getRegisterForTest(1L);
        given(registerServiceMock.getRegisterById(anyLong())).willReturn(Optional.ofNullable(registerForTest));
        given(meterServiceMock.findOne(anyLong())).willReturn(Optional.ofNullable(new Meter()));
        given(registerServiceMock.updateRegister(any(Register.class))).willReturn(registerForTest);

        this.mockMvc.perform(put(MeteringConstants.METERING_BASE_PATH + "/registers/1").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerForTest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.timeFrame", is("OFF_PEAK")))
                .andExpect(jsonPath("$.meteringDirection", is("CONSUMPTION")))
                .andExpect(jsonPath("$.multiplier", is(10)))
                .andExpect(jsonPath("$.registerId", is("123")))
                .andExpect(jsonPath("$.unitOfMeasure", is("KWH")))
                .andExpect(jsonPath("$.numberOfDigits", is(3)));
    }

    @Test
    public void shouldFailUpdateRegisterWhenNoRegisterFound() throws Exception {
        Register registerForTest = getRegisterForTest(1L);
        given(registerServiceMock.getRegisterById(anyLong())).willReturn(Optional.empty());

        this.mockMvc.perform(put(MeteringConstants.METERING_BASE_PATH + "/registers/1").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerForTest)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteRegister() throws Exception {
        Register registerForTest = getRegisterForTest(1L);
        given(registerServiceMock.getRegisterById(anyLong())).willReturn(Optional.ofNullable(registerForTest));
        given(registerServiceMock.updateRegister(any(Register.class))).willReturn(registerForTest);

        this.mockMvc.perform(delete(MeteringConstants.METERING_BASE_PATH + "/registers/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldFailDeleteRegisterWhenNoRegisterFound() throws Exception {
        Register registerForTest = getRegisterForTest(1L);
        given(registerServiceMock.getRegisterById(anyLong())).willReturn(Optional.empty());

        this.mockMvc.perform(delete(MeteringConstants.METERING_BASE_PATH + "/registers/1").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerForTest)))
                .andExpect(status().isNotFound());
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