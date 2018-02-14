package nl.servicehouse.tesla.register.web;

import java.util.Optional;

import javax.validation.Valid;

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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import nl.servicehouse.tesla.common.MeteringConstants;
import nl.servicehouse.tesla.exception.DeleteEntityException;
import nl.servicehouse.tesla.exception.ResourceNotFoundException;
import nl.servicehouse.tesla.meter.Meter;
import nl.servicehouse.tesla.meter.MeterService;
import nl.servicehouse.tesla.register.Register;
import nl.servicehouse.tesla.register.RegisterService;

@RestController
@RequestMapping(path = MeteringConstants.METERING_BASE_PATH + "/registers")
@Api(value = "register")
public class RegisterController {

    private final RegisterService registerService;

    private final MeterService meterService;

    private final ModelMapper modelMapper;

    public RegisterController(RegisterService registerService, MeterService meterService, ModelMapper modelMapper) {
        this.registerService = registerService;
        this.meterService = meterService;
        this.modelMapper = modelMapper;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get registers")
    public ResponseEntity<Page<RegisterDto>> getAllRegisters(final Pageable pageable) {
        Page<Register> registers = registerService.getAllRegisters(pageable);
        return new ResponseEntity<>(registers.map(this::entityToDto), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get register by id")
    public ResponseEntity<RegisterDto> getOne(@PathVariable final Long id) {
        final Register register = registerService.getRegisterById(id)
                .orElseThrow(() -> new IllegalArgumentException("Could not find register for id: " + id));
        return new ResponseEntity<>(entityToDto(register), HttpStatus.OK);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(value = { @ApiResponse(code = 400, message = "Bad request"), @ApiResponse(code = 409, message = "Register could not be created") })
    @ApiOperation(value = "Create new register")
    public HttpHeaders createRegister(@ApiParam(value = "New register to be created", required = true) @RequestBody @Valid final RegisterDto registerDto,
            final BindingResult errors, final UriComponentsBuilder builder) {
        final Register register = registerService.createRegister(dtoToEntity(registerDto));
        final HttpHeaders httpHeaders = new HttpHeaders();
        final UriComponents uriComponents = builder.path(MeteringConstants.METERING_BASE_PATH + "/registers/{registerId}")
                .buildAndExpand(register.getId());
        httpHeaders.setLocation(uriComponents.toUri());
        return httpHeaders;
    }

    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Update existing register")
    public ResponseEntity<RegisterDto> updateRegister(@Validated @RequestBody final RegisterDto registerDto, @PathVariable final Long id,
            final BindingResult errors) {
        if (!registerService.getRegisterById(id)
                .isPresent()) {
            throw new ResourceNotFoundException("resourceNotFound");
        }

        registerDto.setId(id);
        final RegisterDto updatedRegister = entityToDto(registerService.updateRegister(dtoToEntity(registerDto)));
        return new ResponseEntity<>(updatedRegister, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "Delete register with provided id")
    @ApiResponses(value = { @ApiResponse(code = 400, message = "Bad request"), @ApiResponse(code = 404, message = "Register could not be found") })
    public void deleteRegister(@PathVariable("id") Long id) {
        if (!registerService.getRegisterById(id)
                .isPresent()) {
            throw new ResourceNotFoundException("resourceNotFound");
        }

        try {
            registerService.deleteRegister(id);
        } catch (final DataIntegrityViolationException e) {
            throw new DeleteEntityException("DeleteError");
        }
    }

    private RegisterDto entityToDto(final Register register) {
        if (register == null) {
            return null;
        }
        return modelMapper.map(register, RegisterDto.class);
    }

    private Register dtoToEntity(RegisterDto registerDto) {
        Register register;

        if (registerDto.getId() == null) {
            register = modelMapper.map(registerDto, Register.class);
        } else {
            Optional<Register> registerOptional = registerService.getRegisterById(registerDto.getId());
            if (registerOptional.isPresent()) {
                register = registerOptional.get();
                BeanUtils.copyProperties(modelMapper.map(registerDto, Register.class), register, "id");

            } else {
                throw new ResourceNotFoundException("resourceNotFound");
            }

        }
        Meter meter = meterService.findOne(registerDto.getMeterId()).orElseThrow(ResourceNotFoundException::new);
        register.setMeter(meter);

        return register;
    }

}
