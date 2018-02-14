package nl.servicehouse.tesla.volume;

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
public class VolumeServiceImpTest {

    private VolumeServiceImpl volumeService;

    private static final LocalDateTime BEGIN_TIMESTAMP = LocalDateTime.of(2018,12,1,12,23,55);
    private static final LocalDateTime END_TIMESTAMP = LocalDateTime.of(2018,12,1,14,23,55);

    @Mock
    private VolumeRepository volumeRepository;

    @Mock
    private RegisterRepository registerRepository;

    @Before
    public void before() {
        volumeService = new VolumeServiceImpl(volumeRepository);
    }

    @Test
    public void getAllVolumes() {
        when(volumeRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(Arrays.asList(createVolume(1L), createVolume(2L))));

        Page<Volume> volumes = volumeService.getAllVolumes(new PageRequest(0, 10));

        assertThat(volumes.getTotalElements()).isEqualTo(2);
    }

    @Test
    public void getVolumeById() {
        when(volumeRepository.findOne(any(Long.class))).thenReturn(createVolume(1L));

        Optional<Volume> result = volumeService.getVolumeById(1L);

        assertThat(result.isPresent()).isTrue();
        if (result.isPresent()) {
            Volume volume = result.get();
            assertThat(volume.getId()).isEqualTo(1L);
            assertThat(volume.getRequestIdentifier()).isEqualTo("identifier");
            assertThat(volume.getRegister().getId()).isEqualTo(1L);
            assertThat(volume.getValue().getBeginTimestamp()).isEqualTo(BEGIN_TIMESTAMP);
            assertThat(volume.getValue().getEndTimestamp()).isEqualTo(END_TIMESTAMP);
            assertThat(volume.getValue().getValue()).isEqualTo(new BigDecimal(100));
            assertThat(volume.getRegister().getId()).isEqualTo(1L);
        }
    }

    @Test
    public void createVolume() {
        when(volumeRepository.save(any(Volume.class))).thenReturn(createVolume(1L));
        Register register = new Register();
        register.setId(1L);
        when(registerRepository.findOne(anyLong())).thenReturn(register);

        Volume volume = volumeService.createVolume(createVolume(1L));

        assertThat(volume.getId()).isEqualTo(1L);
        assertThat(volume.getRequestIdentifier()).isEqualTo("identifier");
        assertThat(volume.getRegister().getId()).isEqualTo(1L);
        assertThat(volume.getValue().getBeginTimestamp()).isEqualTo(BEGIN_TIMESTAMP);
        assertThat(volume.getValue().getEndTimestamp()).isEqualTo(END_TIMESTAMP);
        assertThat(volume.getValue().getValue()).isEqualTo(new BigDecimal(100));
        assertThat(volume.getRegister().getId()).isEqualTo(1L);
    }

    @Test
    public void updateVolume() {
        Volume mr = createVolume(1L);
        when(volumeRepository.save(any(Volume.class))).thenReturn(mr);
        when(volumeRepository.findOne(any(Long.class))).thenReturn(mr);
        Register register = new Register();
        register.setId(1L);
        when(registerRepository.findOne(anyLong())).thenReturn(register);

        Volume volume = volumeService.createVolume(createVolume(1L));

        assertThat(volume.getId()).isEqualTo(1L);
        assertThat(volume.getRequestIdentifier()).isEqualTo("identifier");
        assertThat(volume.getRegister().getId()).isEqualTo(1L);
        assertThat(volume.getValue().getBeginTimestamp()).isEqualTo(BEGIN_TIMESTAMP);
        assertThat(volume.getValue().getEndTimestamp()).isEqualTo(END_TIMESTAMP);
        assertThat(volume.getValue().getValue()).isEqualTo(new BigDecimal(100));
        assertThat(volume.getRegister().getId()).isEqualTo(1L);
    }

    @Test
    public void deleteVolume() {
        volumeService.deleteVolume(1L);

        Mockito.verify(volumeRepository)
                .delete(1L);
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