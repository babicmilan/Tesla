package nl.servicehouse.billingengine.api.pub.registration;

import org.springframework.validation.DataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

@ControllerAdvice("nl.servicehouse.billingengine.api.pub.registration")
public class DatabindingControllerAdvice {

    @InitBinder
    private void activateDirectFieldAccess(final DataBinder dataBinder) {

        dataBinder.initDirectFieldAccess();
    }

}
