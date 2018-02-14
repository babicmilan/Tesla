package nl.servicehouse.billingengine.metering.consumers;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import nl.servicehouse.billingengine.metering.AccessPointService;
import nl.servicehouse.billingengine.metering.config.MeteringAmqpConfig;
import nl.servicehouse.billingengine.metering.dto.AccessPointDto;

@Component
@RabbitListener(queues = MeteringAmqpConfig.ACCESS_POINT_QUEUE, containerFactory = MeteringAmqpConfig.RABBIT_LISTENER_CONTAINER_BEAN_NAME)
public class AccessPointConsumer {

    private AccessPointService accessPointService;

    @Autowired
    public AccessPointConsumer(AccessPointService accessPointService) {
        this.accessPointService = accessPointService;
    }

    @RabbitHandler
    public void createdAccessPoint(AccessPointDto accessPointDto) {

        //accessPointService.createAccessPoint(accessPointDto);

    }

}
