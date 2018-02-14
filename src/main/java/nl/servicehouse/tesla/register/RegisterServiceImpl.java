package nl.servicehouse.tesla.register;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nl.servicehouse.tesla.meter.MeterRepository;
import nl.servicehouse.tesla.register.web.RegisterDto;

@Service
@Transactional
public class RegisterServiceImpl implements RegisterService {

    private final RegisterRepository registerRepository;
    private final MeterRepository meterRepository;
    private final ModelMapper modelMapper;

    public RegisterServiceImpl(final RegisterRepository registerRepository, MeterRepository meterRepository, ModelMapper modelMapper) {
        this.registerRepository = registerRepository;
        this.meterRepository = meterRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Register> getAllRegisters(Pageable pageable) {
        return registerRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Register> getRegisterById(Long id) {
        return Optional.ofNullable(registerRepository.findOne(id));
    }

    @Override
    public Register createRegister(Register register) {
        return registerRepository.save(register);
    }

    @Override
    public Register updateRegister(Register register) {
        return registerRepository.save(register);
    }

    @Override
    public void deleteRegister(Long id) {
        registerRepository.delete(id);
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
            register = registerRepository.findOne(registerDto.getId());
            BeanUtils.copyProperties(modelMapper.map(registerDto, Register.class), register, "id");
        }

        register.setMeter(meterRepository.findOne(registerDto.getMeterId()));

        return register;
    }
}
