package nl.servicehouse.billingengine.metering;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import nl.servicehouse.billingengine.metering.domain.Register;
import nl.servicehouse.billingengine.metering.domain.Volume;
import nl.servicehouse.billingengine.metering.dto.VolumeDto;
import nl.servicehouse.billingengine.metering.exception.DeleteEntityException;
import nl.servicehouse.billingengine.metering.exception.ResourceNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping(path = MeteringConstants.METERING_BASE_PATH + "/volumes")
@Api(value = "volume")
public class VolumeController {
    
    private final VolumeService volumeService;

    private final RegisterService registerService;
    
    private final ModelMapper modelMapper;

    public VolumeController(final VolumeService volumeService, final RegisterService registerService, final ModelMapper modelMapper) {
        this.volumeService = volumeService;
        this.registerService = registerService;
        this.modelMapper = modelMapper;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get volumes")
    public ResponseEntity<Page<VolumeDto>> getAllVolumes(final Pageable pageable) {
        Page<Volume> volumes = volumeService.getAllVolumes(pageable);
        return new ResponseEntity<>(volumes.map(this::entityToDto), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get volume by id")
    public ResponseEntity<VolumeDto> getOne(@PathVariable final Long id) {
        final Volume volume = volumeService.getVolumeById(id)
                .orElseThrow(() -> new IllegalArgumentException("Could not find volume for id: " + id));
        return new ResponseEntity<>(entityToDto(volume), HttpStatus.OK);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(value = { @ApiResponse(code = 400, message = "Bad request"), @ApiResponse(code = 409, message = "Volume could not be created") })
    @ApiOperation(value = "Create new volume")
    public HttpHeaders createVolume(@ApiParam(value = "New volume to be created", required = true) @RequestBody @Valid final VolumeDto volumeDto,
                                      final BindingResult errors, final UriComponentsBuilder builder) {
        final Volume volume = volumeService.createVolume(dtoToEntity(volumeDto));
        final HttpHeaders httpHeaders = new HttpHeaders();
        final UriComponents uriComponents = builder.path(MeteringConstants.METERING_BASE_PATH + "/volumes/{volumeId}")
                .buildAndExpand(volume.getId());
        httpHeaders.setLocation(uriComponents.toUri());
        return httpHeaders;
    }

    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Update existing volume")
    public ResponseEntity<VolumeDto> updateVolume(@Validated @RequestBody final VolumeDto volumeDto, @PathVariable final Long id,
                                                      final BindingResult errors) {
        if (!volumeService.getVolumeById(id)
                .isPresent()) {
            throw new ResourceNotFoundException("resourceNotFound");
        }

        volumeDto.setId(id);
        final Volume updatedVolume = volumeService.updateVolume(dtoToEntity(volumeDto));
        return new ResponseEntity<>(entityToDto(updatedVolume), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "Delete volume with provided id")
    @ApiResponses(value = { @ApiResponse(code = 400, message = "Bad request"), @ApiResponse(code = 404, message = "Volume could not be found") })
    public void deleteVolume(@PathVariable("id") Long id) {
        if (!volumeService.getVolumeById(id)
                .isPresent()) {
            throw new ResourceNotFoundException("resourceNotFound");
        }

        try {
            volumeService.deleteVolume(id);
        } catch (final DataIntegrityViolationException e) {
            throw new DeleteEntityException("DeleteError");
        }
    }

    private VolumeDto entityToDto(final Volume volume) {
        if (volume == null) {
            return null;
        }
        return modelMapper.map(volume, VolumeDto.class);
    }

    private Volume dtoToEntity(VolumeDto volumeDto) {
        Volume volume;

        if (volumeDto.getId() == null) {
            volume = modelMapper.map(volumeDto, Volume.class);
        } else {
            Optional<Volume> volumeOptional = volumeService.getVolumeById(volumeDto.getId());
            if (volumeOptional.isPresent()) {
                volume = volumeOptional.get();
                BeanUtils.copyProperties(modelMapper.map(volumeDto, Volume.class), volume, "id");
            } else {
                throw new ResourceNotFoundException("resourceNotFound");
            }
        }
        Register register = registerService.getRegisterById(volumeDto.getRegisterId()).get();
        volume.setRegister(register);

        return volume;
    }
}
