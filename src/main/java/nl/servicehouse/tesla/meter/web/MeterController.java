package nl.servicehouse.tesla.meter.web;

import java.util.Optional;

import javax.persistence.TransactionRequiredException;
import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import nl.servicehouse.tesla.accesspoint.AccessPoint;
import nl.servicehouse.tesla.accesspoint.AccessPointService;
import nl.servicehouse.tesla.common.MeteringConstants;
import nl.servicehouse.tesla.exception.DeleteEntityException;
import nl.servicehouse.tesla.exception.PersistEntityException;
import nl.servicehouse.tesla.exception.ResourceNotFoundException;
import nl.servicehouse.tesla.exception.UpdateEntityException;
import nl.servicehouse.tesla.meter.Meter;
import nl.servicehouse.tesla.meter.MeterService;

@RestController
@RequestMapping(path = MeteringConstants.METERING_BASE_PATH + "/meters")
@Api(value = "meter")
public class MeterController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MeterController.class);

    private final MeterService meterService;

    private final AccessPointService accessPointService;

    private final ModelMapper modelMapper;

    public MeterController(final MeterService meterService, AccessPointService accessPointService, ModelMapper modelMapper) {
        this.meterService = meterService;
        this.accessPointService = accessPointService;
        this.modelMapper = modelMapper;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get meters")
    public ResponseEntity<Page<MeterDto>> findAllMeters(final Pageable pageable) {
        Page<Meter> meters = meterService.findAllMeters(pageable);
        return new ResponseEntity<>(meters.map(this::convertToDto), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get meter by id")
    public ResponseEntity<MeterDto> findOne(@PathVariable final Long id) {
        final Meter meter = meterService.findOne(id).orElseThrow(ResourceNotFoundException::new);
        return new ResponseEntity<>(convertToDto(meter), HttpStatus.OK);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Create new meter")
    @ApiResponses(value = { @ApiResponse(code = 400, message = "Bad request"), @ApiResponse(code = 409, message = "Meter could not be created") })
    public HttpHeaders createMeterC(@ApiParam(value = "New meter to be created", required = true) @RequestBody @Valid final MeterDto meterDto,
            final BindingResult errors, final UriComponentsBuilder builder) {
        try {
            final Meter newMeter = meterService.createMeter(convertToEntity(meterDto));
            final HttpHeaders httpHeaders = new HttpHeaders();
            final UriComponents uriComponents = builder.path(MeteringConstants.METERING_BASE_PATH + "/meters/{meterId}")
                    .buildAndExpand(newMeter.getId());
            httpHeaders.setLocation(uriComponents.toUri());
            return httpHeaders;
        } catch (IllegalArgumentException | TransactionRequiredException e) {
            LOGGER.error("Exception ocurred while creating meter with with stack trace {}", e);
            throw new PersistEntityException("createError");
        }
    }

    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/{meterId}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Update meter with provided id")
    @ApiResponses(value = { @ApiResponse(code = 400, message = "Bad request"), @ApiResponse(code = 404, message = "Meter could not be found") })
    public ResponseEntity<MeterDto> updateMeter(
            @ApiParam(value = "MeterId to be update", required = true) @Validated @RequestBody final MeterDto meterDto,
            @PathVariable final Long meterId, final BindingResult errors) {

        try {
            if (!meterService.findOne(meterId)
                    .isPresent()) {
                throw new ResourceNotFoundException("resourceNotFound");
            }

            meterDto.setId(meterId);
            final Meter updatedMeter = meterService.updateMeter(convertToEntity(meterDto));
            return new ResponseEntity<>(convertToDto(updatedMeter), HttpStatus.OK);
        } catch (IllegalArgumentException | TransactionRequiredException e) {
            LOGGER.error("Exception ocurred while updating meter with id {} with stack trace {}", meterId, e);
            throw new UpdateEntityException();
        }
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "Delete meter with provided id")
    @ApiResponses(value = { @ApiResponse(code = 400, message = "Bad request"), @ApiResponse(code = 404, message = "Meter could not be found") })
    public void deleteMeter(@PathVariable("id") Long id) {
        if (!meterService.findOne(id)
                .isPresent()) {
            throw new ResourceNotFoundException("resourceNotFound");
        }
        try {
            meterService.deleteMeter(id);
        } catch (final DataIntegrityViolationException e) {
            LOGGER.error("Constraint violation exception for id", id);
            throw new DeleteEntityException("DeleteError");
        }
    }

    private MeterDto convertToDto(final Meter meter) {
        if (meter == null) {
            return null;
        }
        return modelMapper.map(meter, MeterDto.class);
    }

    private Meter convertToEntity(MeterDto meterDto) {
        Meter meter;

        if (meterDto.getId() == null) {
            meter = modelMapper.map(meterDto, Meter.class);
        } else {
            Optional<Meter> meterOptional = meterService.findOne(meterDto.getId());
            if (meterOptional.isPresent()) {
                meter = meterOptional.get();
                BeanUtils.copyProperties(modelMapper.map(meterDto, Meter.class), meter, "id");

            } else {
                throw new ResourceNotFoundException("resourceNotFound");
            }

        }

        AccessPoint accessPoint = accessPointService.getAccessPointById(meterDto.getAccessPointId()).orElseThrow(ResourceNotFoundException::new);
        meter.setAccessPoint(accessPoint);

        return meter;
    }

}
