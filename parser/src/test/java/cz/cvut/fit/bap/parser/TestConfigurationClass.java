package cz.cvut.fit.bap.parser;


import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;

@TestConfiguration
@EnableRetry
@EnableAsync
public class TestConfigurationClass{
    // Define a mock MeterRegistry bean for testing purposes
    @Bean
    public MeterRegistry meterRegistry(){
        return new SimpleMeterRegistry();
    }
}
