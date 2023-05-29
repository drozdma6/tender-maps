package cz.cvut.fit.bap.parser;


import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestConfigurationClass{
    // Define a mock MeterRegistry bean for testing purposes
    @Bean
    public MeterRegistry meterRegistry(){
        return new SimpleMeterRegistry();
    }
}
