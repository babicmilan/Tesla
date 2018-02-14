package nl.servicehouse.tesla.meterreading.web;

import javax.persistence.TransactionRequiredException;
import javax.validation.Valid;

import io.swagger.annotations.*;
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

import nl.servicehouse.tesla.common.MeteringConstants;
import nl.servicehouse.tesla.exception.DeleteEntityException;
import nl.servicehouse.tesla.exception.PersistEntityException;
import nl.servicehouse.tesla.exception.ResourceNotFoundException;
import nl.servicehouse.tesla.exception.UpdateEntityException;
import nl.servicehouse.tesla.meter.web.MeterController;
import nl.servicehouse.tesla.meterreading.MeterReading;
import nl.servicehouse.tesla.meterreading.MeterReadingService;
import nl.servicehouse.tesla.register.Register;
import nl.servicehouse.tesla.register.RegisterService;

import java.util.Optional;

@RestController("meterreadings")
@RequestMapping(value = MeteringConstants.METERING_BASE_PATH + "/meterreadings")
@Api(value = "MeterReading")
public class MeterReadingController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MeterController.class);

    private final MeterReadingService meterReadingService;

    private final RegisterService registerService;

    private final ModelMapper modelMapper;

    public MeterReadingController(MeterReadingService meterReadingService, RegisterService registerService, ModelMapper modelMapper) {
        this.meterReadingService = meterReadingService;
        this.registerService = registerService;
        this.modelMapper = modelMapper;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get all meter readings")
    @ResponseStatus(HttpStatus.OK)
    public Page<MeterReadingDto> getAllMeterReadings(final Pageable pageable) {
        return this.meterReadingService.getAllMeterReadings(pageable).map(this::mapEntityToDto);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get meter reading by ID")
    public ResponseEntity<MeterReadingDto> getMeterReadingById(@PathVariable final Long id) {
        final MeterReading meterReading = meterReadingService.getMeterReadingById(id).orElseThrow(ResourceNotFoundException::new);
        return new ResponseEntity<>(mapEntityToDto(meterReading), HttpStatus.OK);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Create a new meter reading")
    @ApiResponses(value = { @ApiResponse(code = 400, message = "Bad request"), @ApiResponse(code = 409, message = "Meter reading could not be created") })
    public HttpHeaders createMeterReading(@ApiParam(value = "New meterreading to be created", required = true)@RequestBody @Valid MeterReadingDto meterReadingDto,
                                          final UriComponentsBuilder builder){
        try {
            final MeterReading meterReading = this.meterReadingService.createMeterReading(mapDtoToEntity(meterReadingDto));
            final UriComponents uriComponents = builder.path(MeteringConstants.METERING_BASE_PATH + "/meterreadings/{id}").buildAndExpand(meterReading.getId());
            final HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setLocation(uriComponents.toUri());
            return httpHeaders;
        } catch (IllegalArgumentException | TransactionRequiredException e) {
            LOGGER.error("Exception ocurred while creating meter reading with with stack trace {}", e);
            throw new PersistEntityException("createError");
        }
    }

    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Update meter reading by ID")
    @ApiResponses(value = { @ApiResponse(code = 400, message = "Bad request"), @ApiResponse(code = 404, message = "Meterreading could not be found") })
    public ResponseEntity<MeterReadingDto> updateMeterReading(@RequestBody @Valid MeterReadingDto meterReadingDto, @PathVariable final Long id,
            final BindingResult errors) {
        try {
            if (!meterReadingService.getMeterReadingById(id)
                    .isPresent()) {
                throw new ResourceNotFoundException("resourceNotFound");
            }

            meterReadingDto.setId(id);
            final MeterReadingDto meterReading = mapEntityToDto(meterReadingService.updateMeterReading(mapDtoToEntity(meterReadingDto)));
            return new ResponseEntity<>(meterReading, HttpStatus.OK);
        } catch (IllegalArgumentException | TransactionRequiredException e) {
            LOGGER.error("Exception ocurred while creating meter with with stack trace {}", e);
            throw new UpdateEntityException();
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Delete meter reading by ID")
    @ApiResponses(value = { @ApiResponse(code = 400, message = "Bad request"), @ApiResponse(code = 404, message = "Meterreading could not be found") })
    public void deleteMeterReading(@PathVariable("id") Long id) {
        if (!meterReadingService.getMeterReadingById(id)
                .isPresent()) {
            throw new ResourceNotFoundException("resourceNotFound");
        }
        try {
            meterReadingService.deleteMeterReading(id);
        } catch (final DataIntegrityViolationException e) {
            throw new DeleteEntityException("DeleteError");
        }
    }

    private MeterReading mapDtoToEntity(MeterReadingDto meterReadingDto) {
        MeterReading meterReading;

        if (meterReadingDto.getId() == null) {
            meterReading = modelMapper.map(meterReadingDto, MeterReading.class);
        } else {
            Optional<MeterReading> meterReadingOptional = meterReadingService.getMeterReadingById(meterReadingDto.getId());
            if(meterReadingOptional.isPresent()) {
                meterReading = meterReadingOptional.get();
                BeanUtils.copyProperties(modelMapper.map(meterReadingDto, MeterReading.class), meterReading, "id");
            } else {
                throw new ResourceNotFoundException("resourceNotFound");
            }
        }

        Register register = registerService.getRegisterById(meterReadingDto.getRegisterId()).orElseThrow(ResourceNotFoundException::new);
        meterReading.setRegister(register);

        return meterReading;
    }

    private MeterReadingDto mapEntityToDto(MeterReading meterReading) {
        if(meterReading == null) {
            return null;
        }
        return modelMapper.map(meterReading, MeterReadingDto.class);
    }
}
