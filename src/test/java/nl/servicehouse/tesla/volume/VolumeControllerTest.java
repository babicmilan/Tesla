package nl.servicehouse.tesla.volume;

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

import nl.servicehouse.billingengine.api.pub.registration.DatabindingControllerAdvice;
import nl.servicehouse.billingengine.api.pub.registration.ExceptionControllerAdvice;
import nl.servicehouse.tesla.api.VolumeController;
import nl.servicehouse.tesla.common.MeteringConstants;
import nl.servicehouse.tesla.register.Register;
import nl.servicehouse.tesla.register.RegisterService;

@RunWith(MockitoJUnitRunner.class)
public class VolumeControllerTest {

    private MockMvc mockMvc;
    private ModelMapper modelMapper = new ModelMapper();
    private ObjectMapper objectMapper;

    private static final LocalDateTime BEGIN_TIMESTAMP = LocalDateTime.of(2018,12,1,12,23,55);
    private static final LocalDateTime END_TIMESTAMP = LocalDateTime.of(2018,12,1,14,23,55);
    
    @Mock
    private RegisterService registerService;
    @Mock
    private VolumeService volumeService;

    @Before
    public void before() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(new VolumeController(volumeService, registerService, modelMapper))
                .setControllerAdvice(new DatabindingControllerAdvice(), new ExceptionControllerAdvice())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();

        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JSR310Module());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    public void getAllVolumes() throws Exception {
        given(volumeService.getAllVolumes(any(Pageable.class)))
                .willReturn(new PageImpl<>(Arrays.asList(createVolume(1L), createVolume(2L))));

        this.mockMvc.perform(get(MeteringConstants.METERING_BASE_PATH + "/volumes").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id", is(1)))
                .andExpect(jsonPath("$.content[1].id", is(2)));
    }

    @Test
    public void getVolumeById() throws Exception {
        given(volumeService.getVolumeById(anyLong()))
                .willReturn(Optional.of(createVolume(1L)));

        this.mockMvc.perform(get(MeteringConstants.METERING_BASE_PATH + "/volumes/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())

                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.requestIdentifier", is("identifier")))
                .andExpect(jsonPath("$.registerId", is(1)))
               // .andExpect(jsonPath("$.value.beginTimestamp", is(BEGIN_TIMESTAMP)))
                //.andExpect(jsonPath("$.value.endTimestamp", is(END_TIMESTAMP)))
                .andExpect(jsonPath("$.value.value", is(100)));
    }

    @Test
    public void createVolume() throws Exception {
        given(volumeService.createVolume(any(Volume.class)))
                .willReturn(createVolume(1L));
        given(registerService.getRegisterById(anyLong())).willReturn(Optional.ofNullable(new Register()));
        given(volumeService.getVolumeById(anyLong())).willReturn(Optional.of(createVolume(1L)));
        this.mockMvc.perform(post(MeteringConstants.METERING_BASE_PATH + "/volumes").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createVolume(1L))))
                .andExpect(status().isCreated())
                .andExpect(header().stringValues("Location", "http://localhost" + MeteringConstants.METERING_BASE_PATH + "/volumes/1"));
    }

    @Test
    public void updateVolume() throws Exception{
        Volume volume = createVolume(1L);
        given(volumeService.getVolumeById(anyLong()))
                .willReturn(Optional.of(volume));
        given(registerService.getRegisterById(anyLong())).willReturn(Optional.ofNullable(new Register()));
        given(volumeService.updateVolume(any(Volume.class)))
                .willReturn(volume);

        this.mockMvc.perform(put(MeteringConstants.METERING_BASE_PATH + "/volumes/1").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createVolume(1L))))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteVolume() throws Exception {
        Volume volumeForTest = createVolume(1L);
        given(volumeService.getVolumeById(anyLong())).willReturn(Optional.ofNullable(volumeForTest));
        given(volumeService.updateVolume(any(Volume.class))).willReturn(volumeForTest);

        this.mockMvc.perform(delete(MeteringConstants.METERING_BASE_PATH + "/volumes/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldFailDeleteVolumeWhenNoVolumeFound() throws Exception {
        Volume volumeForTest = createVolume(1L);
        given(volumeService.getVolumeById(anyLong())).willReturn(Optional.empty());

        this.mockMvc.perform(delete(MeteringConstants.METERING_BASE_PATH + "/volumes/1").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(volumeForTest)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldFailUpdateRegisterWhenNoRegisterFound() throws Exception {
        Volume volumeForTest = createVolume(1L);
        given(volumeService.getVolumeById(anyLong())).willReturn(Optional.empty());

        this.mockMvc.perform(put(MeteringConstants.METERING_BASE_PATH + "/volumes/1").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(volumeForTest)))
                .andExpect(status().isNotFound());
    }

    private Volume createVolume(Long id) {
        Volume volume = new Volume();
        volume.setId(id);
        Register register = new Register();
        register.setId(1L);
        volume.setRegister(register);
        volume.setRequestIdentifier("identifier");
        Value value =  new Value();
        value.setBeginTimestamp(BEGIN_TIMESTAMP);
        value.setEndTimestamp(END_TIMESTAMP);
        value.setValue(new BigDecimal(100));
        volume.setValue(value);
        return volume;
    }
}
