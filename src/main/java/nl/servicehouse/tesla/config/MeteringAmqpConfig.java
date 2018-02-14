package nl.servicehouse.tesla.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpConnectException;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MeteringAmqpConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(MeteringAmqpConfig.class);
    public static final String TOPIC_EXCHANGE = "amq.topic";
    public static final String ACCESS_POINT_QUEUE = "accessPointQueue";
    public static final String RABBIT_LISTENER_CONTAINER_BEAN_NAME = "simpleRabbitListenerContainerFactory";
    public static final String ROUTING_KEY = "event.accesspoint.created";

    @Bean
    public ConnectionFactory connectionFactory(@Value("${spring.rabbitmq.host}") final String host,
                                               @Value("${spring.rabbitmq.username}") final String username, @Value("${spring.rabbitmq.password}") final String password) {
        final CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(host);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        return connectionFactory;
    }

    @Bean
    public RabbitAdmin rabbitAdmin(final ConnectionFactory connectionFactory, final ApplicationContext applicationContext) {
        final RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.setApplicationContext(applicationContext);
        try {
            rabbitAdmin.initialize();
        } catch (final AmqpConnectException ce) {
            LOGGER.error("Connection to RabbitMQ server failed: " + ce.getMessage());
        }
        return rabbitAdmin;
    }

    @Bean
    public RabbitTemplate meteringRabbitTemplate(final ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jacksonConverter());
        return rabbitTemplate;
    }

    @Bean
    public Jackson2JsonMessageConverter jacksonConverter() {
        return  new Jackson2JsonMessageConverter();
    }

    @Bean(name = RABBIT_LISTENER_CONTAINER_BEAN_NAME)
    public SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory(final ConnectionFactory connectionFactory) {
        final SimpleRabbitListenerContainerFactory containerFactory = new SimpleRabbitListenerContainerFactory();
        containerFactory.setConnectionFactory(connectionFactory);
        containerFactory.setConcurrentConsumers(3);
        containerFactory.setMaxConcurrentConsumers(10);
        containerFactory.setMessageConverter(jacksonConverter());
        return containerFactory;
    }

    @Bean
    public Exchange exchange(){
        return new TopicExchange(TOPIC_EXCHANGE);
    }

    @Bean
    public Queue queue() {
        return new Queue(ACCESS_POINT_QUEUE);
    }

    @Bean
    public Binding binding(Queue queue, Exchange exchange) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with(ROUTING_KEY).noargs();
    }
}
