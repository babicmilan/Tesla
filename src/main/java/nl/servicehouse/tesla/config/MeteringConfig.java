package nl.servicehouse.billingengine.metering.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MeteringConfig {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
