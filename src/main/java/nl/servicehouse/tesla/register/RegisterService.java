package nl.servicehouse.tesla.register;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface RegisterService {

    Page<Register> getAllRegisters(Pageable pageable);

    Optional<Register> getRegisterById(Long id);

    Register createRegister(Register register);

    Register updateRegister(Register register);

    void deleteRegister(Long id);
}
