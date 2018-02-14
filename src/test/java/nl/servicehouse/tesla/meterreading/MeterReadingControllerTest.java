package nl.servicehouse.tesla.meterreading;

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
import java.time.LocalDateTime;
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
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;

import nl.servicehouse.tesla.api.MeterReadingController;
import nl.servicehouse.tesla.common.DatabindingControllerAdvice;
import nl.servicehouse.tesla.common.ExceptionControllerAdvice;
import nl.servicehouse.tesla.common.MeteringConstants;
import nl.servicehouse.tesla.register.Register;
import nl.servicehouse.tesla.register.RegisterService;

@RunWith(MockitoJUnitRunner.class)
public class MeterReadingControllerTest {

    private MockMvc mockMvc;
    private ModelMapper modelMapper = new ModelMapper();
    private ObjectMapper objectMapper;
    
    @Mock
    private RegisterService registerService;
    @Mock
    private MeterReadingService meterReadingService;

    @Before
    public void before() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(new MeterReadingController(meterReadingService, registerService, modelMapper))
                .setControllerAdvice(new DatabindingControllerAdvice(), new ExceptionControllerAdvice())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();

        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JSR310Module());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    public void getAllMeterReadings() throws Exception {
        given(meterReadingService.getAllMeterReadings(any(Pageable.class)))
                .willReturn(new PageImpl<>(Arrays.asList(createMeterReading(1L), createMeterReading(2L))));

        this.mockMvc.perform(get(MeteringConstants.METERING_BASE_PATH + "/meterreadings").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id", is(1)))
                .andExpect(jsonPath("$.content[1].id", is(2)));
    }

    @Test
    public void getMeterReadingById() throws Exception {
        given(meterReadingService.getMeterReadingById(anyLong()))
                .willReturn(Optional.of(createMeterReading(1L)));

        this.mockMvc.perform(get(MeteringConstants.METERING_BASE_PATH + "/meterreadings/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.registerId", is(1)))
                .andExpect(jsonPath("$.orderIdentifier", is("order")))
                .andExpect(jsonPath("$.typeOfOrigin", is("AGREED")))
                .andExpect(jsonPath("$.value", is(5)));
    }

    @Test
    public void createMeterReading() throws Exception {
        given(meterReadingService.createMeterReading(any(MeterReading.class)))
                .willReturn(createMeterReading(1L));
        given(registerService.getRegisterById(anyLong())).willReturn(Optional.ofNullable(new Register()));
        given(meterReadingService.getMeterReadingById(anyLong())).willReturn(Optional.of(createMeterReading(1L)));
        this.mockMvc.perform(post(MeteringConstants.METERING_BASE_PATH + "/meterreadings").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createMeterReading(1L))))
                .andExpect(status().isCreated())
                .andExpect(header().stringValues("Location", "http://localhost" + MeteringConstants.METERING_BASE_PATH + "/meterreadings/1"));
    }

    @Test
    public void updateMeterReading() throws Exception{
        MeterReading meterReading = createMeterReading(1L);
        given(meterReadingService.getMeterReadingById(anyLong()))
                .willReturn(Optional.of(meterReading));
        given(registerService.getRegisterById(anyLong())).willReturn(Optional.ofNullable(new Register()));
        given(meterReadingService.updateMeterReading(any(MeterReading.class)))
                .willReturn(meterReading);

        this.mockMvc.perform(put(MeteringConstants.METERING_BASE_PATH + "/meterreadings/1").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createMeterReading(1L))))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteMeterReading() throws Exception {
        MeterReading meterReadingForTest = createMeterReading(1L);
        given(meterReadingService.getMeterReadingById(anyLong())).willReturn(Optional.ofNullable(meterReadingForTest));
        given(meterReadingService.updateMeterReading(any(MeterReading.class))).willReturn(meterReadingForTest);

        this.mockMvc.perform(delete(MeteringConstants.METERING_BASE_PATH + "/meterreadings/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldFailDeleteMeterReadingWhenNoMeterReadingFound() throws Exception {
        MeterReading meterReadingForTest = createMeterReading(1L);
        given(meterReadingService.getMeterReadingById(anyLong())).willReturn(Optional.empty());

        this.mockMvc.perform(delete(MeteringConstants.METERING_BASE_PATH + "/meterreadings/1").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(meterReadingForTest)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldFailUpdateRegisterWhenNoRegisterFound() throws Exception {
        MeterReading meterReadingForTest = createMeterReading(1L);
        given(meterReadingService.getMeterReadingById(anyLong())).willReturn(Optional.empty());

        this.mockMvc.perform(put(MeteringConstants.METERING_BASE_PATH + "/meterreadings/1").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(meterReadingForTest)))
                .andExpect(status().isNotFound());
    }

    private MeterReading createMeterReading(Long id) {
        MeterReading meterReading = new MeterReading();
        meterReading.setId(id);
        Register register = new Register();
        register.setId(1L);
        meterReading.setRegister(register);
        meterReading.setOrderIdentifier("order");
        meterReading.setTypeOfOrigin(TypeOfOrigin.AGREED);
        meterReading.setValue(new BigDecimal(5));
        meterReading.setReadingTimestamp(LocalDateTime.of(2,2,2,2,2));
        meterReading.setRequestTimestamp(LocalDateTime.of(2,2,2,2,2));
        return meterReading;
    }
}
