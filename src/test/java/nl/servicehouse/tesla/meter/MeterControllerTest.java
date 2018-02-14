package nl.servicehouse.tesla.meter;

import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
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

import nl.servicehouse.tesla.accesspoint.AccessPoint;
import nl.servicehouse.tesla.accesspoint.AccessPointService;
import nl.servicehouse.tesla.api.MeterController;
import nl.servicehouse.tesla.common.DatabindingControllerAdvice;
import nl.servicehouse.tesla.common.ExceptionControllerAdvice;
import nl.servicehouse.tesla.common.MeteringConstants;

@RunWith(MockitoJUnitRunner.class)
public class MeterControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private MeterService meterServiceMock;

    @Mock
    private AccessPointService accessPointService;
    
    ModelMapper modelMapper = new ModelMapper();

    @Before
    public void setUp() throws Exception {

        this.mockMvc = MockMvcBuilders.standaloneSetup(new MeterController(meterServiceMock, accessPointService, modelMapper))
                .setControllerAdvice(new DatabindingControllerAdvice(), new ExceptionControllerAdvice())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();

        this.objectMapper = new ObjectMapper();

        objectMapper.registerModule(new JSR310Module());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    }

    @Test
    public void getAllMeters() throws Exception {
        given(meterServiceMock.findAllMeters(any(Pageable.class))).willReturn(
                new PageImpl<>(Arrays.asList(createMeterForTest(1L), createMeterForTest(2L))));

        this.mockMvc.perform(get(MeteringConstants.METERING_BASE_PATH + "/meters").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id", is(1)))
                .andExpect(jsonPath("$.content[1].id", is(2)));
    }

    @Test
    public void getOne() throws Exception {
        given(meterServiceMock.findOne(anyLong())).willReturn(Optional.of(createMeterForTest(1L)));

        this.mockMvc.perform(get(MeteringConstants.METERING_BASE_PATH + "/meters/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.meterNumber", is("12345")))
                .andExpect(jsonPath("$.accessPointId", is(1)))
                //.andExpect(jsonPath("$.activationDate", is(LocalDate.now())))
                //.andExpect(jsonPath("$.deactivationDate", is(LocalDate.now().plusYears(2))))
                .andExpect(jsonPath("$.buildYear", is(2018)))
                .andExpect(jsonPath("$.meteringType", is("MANUAL")))
                .andExpect(jsonPath("$.description", is("Description")))
                .andExpect(jsonPath("$.status", is("ACTIVE")))
                .andExpect(jsonPath("$.meterReadingFrequency", is("YEARLY")));
    }

    @Test
    public void createMeter() throws Exception {
        Meter meter = new Meter();
        meter.setId(10L);
        given(meterServiceMock.findOne(anyLong())).willReturn(Optional.of(createMeterForTest(1L)));
        given(accessPointService.getAccessPointById(anyLong())).willReturn(Optional.ofNullable(new AccessPoint()));
        given(meterServiceMock.createMeter(any(Meter.class))).willReturn(meter);

        this.mockMvc.perform(post(MeteringConstants.METERING_BASE_PATH + "/meters").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createMeterForTest(1L))))
                .andExpect(status().isCreated())
                .andExpect(header().stringValues("Location", "http://localhost" + MeteringConstants.METERING_BASE_PATH + "/meters/10"))
        .andDo(print());
    }

    @Test
    public void updateMeter() throws Exception {
        Meter meterForTest = createMeterForTest(1L);
        given(meterServiceMock.findOne(anyLong())).willReturn(Optional.ofNullable(meterForTest));
        given(accessPointService.getAccessPointById(anyLong())).willReturn(Optional.ofNullable(new AccessPoint()));
        given(meterServiceMock.updateMeter(any(Meter.class))).willReturn(meterForTest);

        this.mockMvc.perform(put(MeteringConstants.METERING_BASE_PATH + "/meters/1").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(meterForTest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.meterNumber", is("12345")))
                //.andExpect(jsonPath("$.activationDate", is(LocalDate.now())))
                //.andExpect(jsonPath("$.deactivationDate", is(LocalDate.now().plusYears(2))))
                .andExpect(jsonPath("$.buildYear", is(2018)))
                .andExpect(jsonPath("$.meteringType", is("MANUAL")))
                .andExpect(jsonPath("$.description", is("Description")))
                .andExpect(jsonPath("$.status", is("ACTIVE")))
                .andExpect(jsonPath("$.meterReadingFrequency", is("YEARLY")));
    }

    @Test
    public void shouldFailUpdateMeterWhenNoMeterFound() throws Exception {
        Meter meterForTest = createMeterForTest(1L);
        given(meterServiceMock.findOne(anyLong())).willReturn(Optional.empty());

        this.mockMvc.perform(put(MeteringConstants.METERING_BASE_PATH + "/meters/1").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(meterForTest)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteMeter() throws Exception {
        Meter meterForTest = createMeterForTest(1L);
        given(meterServiceMock.findOne(anyLong())).willReturn(Optional.ofNullable(meterForTest));
        given(meterServiceMock.updateMeter(any(Meter.class))).willReturn(meterForTest);

        this.mockMvc.perform(delete(MeteringConstants.METERING_BASE_PATH + "/meters/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldFailDeleteMeterWhenNoMeterFound() throws Exception {
        Meter meterForTest = createMeterForTest(1L);
        given(meterServiceMock.findOne(anyLong())).willReturn(Optional.empty());

        this.mockMvc.perform(delete(MeteringConstants.METERING_BASE_PATH + "/meters/1").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(meterForTest)))
                .andExpect(status().isNotFound());
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