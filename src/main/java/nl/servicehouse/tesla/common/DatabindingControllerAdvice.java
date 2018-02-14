package nl.servicehouse.tesla.common;

import org.springframework.validation.DataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

@ControllerAdvice("nl.servicehouse.tesla.common")
public class DatabindingControllerAdvice {

    @InitBinder
    private void activateDirectFieldAccess(final DataBinder dataBinder) {

        dataBinder.initDirectFieldAccess();
    }

}
