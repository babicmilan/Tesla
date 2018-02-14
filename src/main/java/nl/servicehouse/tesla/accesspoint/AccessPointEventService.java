package nl.servicehouse.tesla.accesspoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import nl.servicehouse.billingengine.metering.config.MeteringAmqpConfig;
import nl.servicehouse.billingengine.metering.dto.AccessPointDto;

@Service
public class AccessPointEventService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccessPointEventService.class);

    private final RabbitTemplate rabbitTemplate;

    public AccessPointEventService(@Qualifier("meteringRabbitTemplate") RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendAccessPointCreatedEvent(final AccessPointDto accessPointDto) {
        LOGGER.info("Send access point created event message");
        rabbitTemplate.convertAndSend(MeteringAmqpConfig.TOPIC_EXCHANGE, MeteringAmqpConfig.ROUTING_KEY, accessPointDto);
    }
}
