package nl.servicehouse.tesla.accesspoint;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

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

@RunWith(MockitoJUnitRunner.class)
public class AccessPointServiceImpTest {

    @Mock
    private AccessPointRepository accessPointRepository;

    private AccessPointServiceImpl accessPointService;

    @Before
    public void before(){
        accessPointService = new AccessPointServiceImpl(accessPointRepository);
    }

    @Test
    public void getAllAcessPoints(){
        when(accessPointRepository.findAll(any(Pageable.class))).
                thenReturn(new PageImpl<>(Arrays.asList(createAccessPoint(1L, "NL4323434234563"), createAccessPoint(2L, "NL4323434234563"))));

        Page<AccessPoint> accessPoints = accessPointService.getAllAccessPoints(new PageRequest(0, 20));

        assertThat(accessPoints.getTotalElements()).isEqualTo(2);
    }

    @Test
    public void getById() {
        when(accessPointRepository.findOne(anyLong())).thenReturn(createAccessPoint(1L, "NL4323434234563"));

        Optional<AccessPoint> accessPointOptional = accessPointService.getAccessPointById(1L);

        if(accessPointOptional.isPresent()) {
            AccessPoint accessPoint = accessPointOptional.get();

            assertThat(accessPoint.getId()).isEqualTo(1);
            assertThat(accessPoint.getStatus()).isEqualTo(AccessPointStatus.ACTIVE);
            assertThat(accessPoint.getCalendar()).isEqualTo(Calendar.P_7_21);
            assertThat(accessPoint.getCommodity()).isEqualTo(Commodity.ELECTRICITY);
            assertThat(accessPoint.getDescription()).isEqualTo("description");
            assertThat(accessPoint.getConnectionId()).isEqualTo(15L);
            assertThat(accessPoint.getEan()).isEqualTo("NL4323434234563");
            assertThat(accessPoint.getGridAreaEan()).isEqualTo("ATEA");
        }

    }

    @Test
    public void getByEan() {
        when(accessPointRepository.findByEan(anyString())).thenReturn(createAccessPoint(1L, "NL4323434234563"));

        AccessPoint accessPoint = accessPointService.getAccessPointByEAN("NL4323434234563");

        assertThat(accessPoint.getId()).isEqualTo(1);
        assertThat(accessPoint.getStatus()).isEqualTo(AccessPointStatus.ACTIVE);
        assertThat(accessPoint.getCalendar()).isEqualTo(Calendar.P_7_21);
        assertThat(accessPoint.getCommodity()).isEqualTo(Commodity.ELECTRICITY);
        assertThat(accessPoint.getDescription()).isEqualTo("description");
        assertThat(accessPoint.getConnectionId()).isEqualTo(15L);
        assertThat(accessPoint.getEan()).isEqualTo("NL4323434234563");
        assertThat(accessPoint.getGridAreaEan()).isEqualTo("ATEA");
    }

    @Test
    public void createAccessPoint() {
        when(accessPointRepository.save(any(AccessPoint.class))).thenReturn(createAccessPoint(1L, "NL4323434234563"));

        AccessPoint accessPoint = accessPointService.createAccessPoint(createAccessPoint(1L, "NL4323434234563"));

        assertThat(accessPoint.getId()).isEqualTo(1);
        assertThat(accessPoint.getStatus()).isEqualTo(AccessPointStatus.ACTIVE);
        assertThat(accessPoint.getCalendar()).isEqualTo(Calendar.P_7_21);
        assertThat(accessPoint.getCommodity()).isEqualTo(Commodity.ELECTRICITY);
        assertThat(accessPoint.getDescription()).isEqualTo("description");
        assertThat(accessPoint.getConnectionId()).isEqualTo(15L);
        assertThat(accessPoint.getEan()).isEqualTo("NL4323434234563");
        assertThat(accessPoint.getGridAreaEan()).isEqualTo("ATEA");
    }

    @Test
    public void updateAccessPoint() {
        when(accessPointRepository.save(any(AccessPoint.class))).thenReturn(createAccessPoint(1L, "NL4323434234563"));
        when(accessPointRepository.findOne(anyLong())).thenReturn(createAccessPoint(1L, "NL4323434234563"));

        AccessPoint accessPoint = accessPointService.updateAccessPoint(createAccessPoint(1L, "NL4323434234563"));

        assertThat(accessPoint.getId()).isEqualTo(1);
        assertThat(accessPoint.getStatus()).isEqualTo(AccessPointStatus.ACTIVE);
        assertThat(accessPoint.getCalendar()).isEqualTo(Calendar.P_7_21);
        assertThat(accessPoint.getCommodity()).isEqualTo(Commodity.ELECTRICITY);
        assertThat(accessPoint.getDescription()).isEqualTo("description");
        assertThat(accessPoint.getConnectionId()).isEqualTo(15L);
        assertThat(accessPoint.getEan()).isEqualTo("NL4323434234563");
        assertThat(accessPoint.getGridAreaEan()).isEqualTo("ATEA");
    }

    @Test
    public void deleteAccessPoint() {
        accessPointService.deleteAccessPoint(1L);

        Mockito.verify(accessPointRepository)
                .delete(1L);
    }

    private AccessPoint createAccessPoint(Long id, String ean) {
        AccessPoint accessPoint = new AccessPoint();
        accessPoint.setId(id);
        accessPoint.setStatus(AccessPointStatus.ACTIVE);
        accessPoint.setCalendar(Calendar.P_7_21);
        accessPoint.setCommodity(Commodity.ELECTRICITY);
        accessPoint.setDescription("description");
        accessPoint.setConnectionId(15L);
        accessPoint.setEan(ean);
        accessPoint.setGridAreaEan("ATEA");
        return accessPoint;
    }
}
