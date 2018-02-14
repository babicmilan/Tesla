package nl.servicehouse.tesla.accesspoint.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import nl.servicehouse.billingengine.metering.domain.AccessPoint;
import nl.servicehouse.billingengine.metering.dto.AccessPointDto;
import nl.servicehouse.billingengine.metering.exception.PersistEntityException;
import nl.servicehouse.billingengine.metering.exception.ResourceNotFoundException;
import nl.servicehouse.billingengine.metering.exception.UpdateEntityException;
import nl.servicehouse.tesla.common.MeteringConstants;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

import javax.persistence.TransactionRequiredException;
import javax.validation.Valid;


@RestController
@RequestMapping(MeteringConstants.METERING_BASE_PATH + "/accesspoints")
@Api(value = "accesspoints")
public class AccessPointController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccessPointController.class);

    private final AccessPointService accessPointService;

    private final AccessPointEventService eventService;

    private final ModelMapper modelMapper;

    public AccessPointController(AccessPointService accessPointService, AccessPointEventService eventService, ModelMapper modelMapper) {
        this.accessPointService = accessPointService;
        this.eventService = eventService;
        this.modelMapper = modelMapper;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get all access points")
    @ResponseStatus(HttpStatus.OK)
    public Page<AccessPointDto> getAllAccessPoints(final Pageable pageable) {
        Page<AccessPoint> accessPoints = this.accessPointService.getAllAccessPoints(pageable);
        return accessPoints.map(this::mapEntityToDto);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get access point by ID")
    public ResponseEntity<AccessPointDto> getAccessPointById(@PathVariable final Long id) {
        final AccessPoint accessPoint = this.accessPointService.getAccessPointById(id).orElseThrow(ResourceNotFoundException::new);
        if (accessPoint != null) {
            return new ResponseEntity<>(mapEntityToDto(accessPoint), HttpStatus.OK);
        } else {
            throw new ResourceNotFoundException();
        }
    }

    @GetMapping(value = "/ean/{ean}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get access point by EAN")
    public ResponseEntity<AccessPointDto> getAccessPointByEAN(@PathVariable("ean") String ean) {
        AccessPoint accessPoint = this.accessPointService.getAccessPointByEAN(ean);
        if (accessPoint != null) {
            return new ResponseEntity<>(mapEntityToDto(accessPoint), HttpStatus.OK);
        } else {
            throw new ResourceNotFoundException();
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Create new access point")
    @ApiResponses(value = { @ApiResponse(code = 400, message = "Bad request"), @ApiResponse(code = 409, message = "Access point could not be created") })
    public HttpHeaders createAccessPoint(@ApiParam(value = "New access point", required = true) @RequestBody @Valid AccessPointDto accessPointDto, final UriComponentsBuilder builder) {
        try {
            final AccessPoint accessPoint = this.accessPointService.createAccessPoint(mapDtoToEntity(accessPointDto));
            final UriComponents uriComponents = builder.path(MeteringConstants.METERING_BASE_PATH + "/accesspoints/{id}")
                    .buildAndExpand(accessPoint.getId());
            eventService.sendAccessPointCreatedEvent(accessPointDto);
            final HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setLocation(uriComponents.toUri());
            return httpHeaders;
        } catch (IllegalArgumentException | TransactionRequiredException e) {
            LOGGER.error("Exception ocurred while creating meter with with stack trace {}", e);
            throw new PersistEntityException();
        }
    }

    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/{id}")
    @ApiOperation(value = "Update access point by ID")
    @ApiResponses(value = { @ApiResponse(code = 400, message = "Bad request"), @ApiResponse(code = 404, message = "Access point could not be found") })
    public ResponseEntity<AccessPointDto> updateAccessPoint(@ApiParam(value = "Updated access point", required = true) @RequestBody @Valid AccessPointDto accessPointDto,
                                                            @PathVariable final Long id) {
        try {
            if (!accessPointService.getAccessPointById(id)
                    .isPresent()) {
                throw new ResourceNotFoundException("resourceNotFound");
            }
            accessPointDto.setId(id);
            AccessPoint accessPoint = this.accessPointService.updateAccessPoint(mapDtoToEntity(accessPointDto));

            return new ResponseEntity<>(mapEntityToDto(accessPoint), HttpStatus.OK);

        } catch (IllegalArgumentException | TransactionRequiredException e) {
            LOGGER.error("Exception ocurred while updating accesspoint with id {} with stack trace {}", id, e);
            throw new UpdateEntityException();
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Delete access point by ID")
    @ApiResponses(value = { @ApiResponse(code = 400, message = "Bad request"), @ApiResponse(code = 404, message = "Access point could not be found") })
    public void deleteAccessPoint(@PathVariable("id") Long id) {
        this.accessPointService.deleteAccessPoint(id);
    }

    private AccessPoint mapDtoToEntity(AccessPointDto accessPointDto) {
        if(accessPointDto == null){
            return null;
        }
        return modelMapper.map(accessPointDto, AccessPoint.class);
    }

    private AccessPointDto mapEntityToDto(AccessPoint accessPoint) {
        if(accessPoint == null) {
            return null;
        }
        return modelMapper.map(accessPoint, AccessPointDto.class);
    }
}
