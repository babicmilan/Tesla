package nl.servicehouse.tesla.accesspoint;

import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import nl.servicehouse.tesla.accesspoint.web.AccessPointController;
import nl.servicehouse.tesla.common.MeteringConstants;

@RunWith(MockitoJUnitRunner.class)
public class AccessPointControllerTest {
    
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private AccessPointService accessPointService;

    private ModelMapper modelMapper;

    @Mock
    private AccessPointEventService eventService;

    @Before
    public void before() {
        modelMapper = new ModelMapper();
        
        this.mockMvc = MockMvcBuilders.standaloneSetup(new AccessPointController(accessPointService, eventService, modelMapper))
                .setControllerAdvice(new DatabindingControllerAdvice(), new ExceptionControllerAdvice())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();

        this.objectMapper = new ObjectMapper();
    }

    @Test
    public void getAllAcessPoints() throws Exception{
        given(accessPointService.getAllAccessPoints(any(Pageable.class)))
                .willReturn(new PageImpl<>(Arrays.asList(createAccessPoint(1L, "NL4323434234563"), createAccessPoint(2L, "NL4323434234563"))));

        this.mockMvc.perform(get(MeteringConstants.METERING_BASE_PATH + "/accesspoints").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id", is(1)))
                .andExpect(jsonPath ("$.content[1].id", is(2)));
    }

    @Test
    public void getById() throws Exception {
        given(accessPointService.getAccessPointById(anyLong()))
                .willReturn(Optional.of(createAccessPoint(1L, "NL4323434234563")));

        this.mockMvc.perform(get(MeteringConstants.METERING_BASE_PATH + "/accesspoints/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.status", is("ACTIVE")))
                .andExpect(jsonPath("$.calendar", is("P_7_21")))
                .andExpect(jsonPath("$.commodity", is("ELECTRICITY")))
                .andExpect(jsonPath("$.description", is("description")))
                .andExpect(jsonPath("$.connectionId", is(5)))
                .andExpect(jsonPath("$.ean", is("NL4323434234563")))
                .andExpect(jsonPath("$.gridAreaEan", is("ATEA")))
                .andExpect(jsonPath("$.calendar", is("P_7_21")));
    }

    @Test
    public void getByEAN() throws Exception {
        given(accessPointService.getAccessPointByEAN(anyString()))
                .willReturn(createAccessPoint(1L, "NL4323434234563"));

        this.mockMvc.perform(get(MeteringConstants.METERING_BASE_PATH + "/accesspoints/ean/NL4323434234563").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.status", is("ACTIVE")))
                .andExpect(jsonPath("$.calendar", is("P_7_21")))
                .andExpect(jsonPath("$.commodity", is("ELECTRICITY")))
                .andExpect(jsonPath("$.description", is("description")))
                .andExpect(jsonPath("$.connectionId", is(5)))
                .andExpect(jsonPath("$.ean", is("NL4323434234563")))
                .andExpect(jsonPath("$.gridAreaEan", is("ATEA")))
                .andExpect(jsonPath("$.calendar", is("P_7_21")));
    }

    @Test
    public void createAccessPoint() throws Exception {
        AccessPoint accessPoint = new AccessPoint();
        accessPoint.setId(10L);
        given(accessPointService.createAccessPoint(any(AccessPoint.class)))
                .willReturn(accessPoint);

        this.mockMvc.perform(post(MeteringConstants.METERING_BASE_PATH + "/accesspoints").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createAccessPoint(1L,"NL4323434234563"))))
                .andExpect(status().isCreated())
                .andExpect(header().stringValues("Location", "http://localhost" + MeteringConstants.METERING_BASE_PATH + "/accesspoints/10"));
    }

    @Test
    public void updateAccessPoint() throws Exception {
        AccessPoint accessPoint = createAccessPoint(1L, "NL4323434234563");
        given(accessPointService.getAccessPointById(anyLong())).willReturn(Optional.ofNullable(new AccessPoint()));
        given(accessPointService.updateAccessPoint(any(AccessPoint.class)))
                .willReturn(accessPoint);

        this.mockMvc.perform(put(MeteringConstants.METERING_BASE_PATH + "/accesspoints/1").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accessPoint)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.status", is("ACTIVE")))
                .andExpect(jsonPath("$.calendar", is("P_7_21")))
                .andExpect(jsonPath("$.commodity", is("ELECTRICITY")))
                .andExpect(jsonPath("$.description", is("description")))
                .andExpect(jsonPath("$.connectionId", is(5)))
                .andExpect(jsonPath("$.ean", is("NL4323434234563")))
                .andExpect(jsonPath("$.gridAreaEan", is("ATEA")))
                .andExpect(jsonPath("$.calendar", is("P_7_21")));
    }

    @Test
    public void deleteAccessPoint() throws  Exception {
        AccessPoint accessPoint = createAccessPoint(1L, "NL4323434234563");

        this.mockMvc.perform(delete(MeteringConstants.METERING_BASE_PATH + "/accesspoints/1").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accessPoint)))
                .andExpect(status().isOk());
    }

    private AccessPoint createAccessPoint(Long id, String ean) {
        AccessPoint accessPoint = new AccessPoint();
        accessPoint.setId(id);
        accessPoint.setStatus(AccessPointStatus.ACTIVE);
        accessPoint.setCalendar(Calendar.P_7_21);
        accessPoint.setCommodity(Commodity.ELECTRICITY);
        accessPoint.setDescription("description");
        accessPoint.setConnectionId(5L);
        accessPoint.setEan(ean);
        accessPoint.setGridAreaEan("ATEA");
        return accessPoint;
    }
}