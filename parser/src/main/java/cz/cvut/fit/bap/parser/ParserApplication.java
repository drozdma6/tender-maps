package cz.cvut.fit.bap.parser;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@SpringBootApplication
@EnableScheduling
@EnableAsync
@EnableRetry
public class ParserApplication{
    @Value("${core.pool.size}")
    private Integer corePoolSize;

    public static void main(String[] args){
        SpringApplication.run(ParserApplication.class, args);
    }

    /*
        Bean enabling @Timed annotation
     */
    @Bean
    public TimedAspect timedAspect(MeterRegistry registry){
        return new TimedAspect(registry);
    }

    /*
        Common tag for each metric
     */
    @Bean
    MeterRegistryCustomizer<MeterRegistry> configurer(){
        return registry -> registry.config().commonTags("application", "scrapper");
    }

    /*
        Creation of thread pool executor to enable multithreading
     */
    @Bean
    public Executor taskExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(corePoolSize);
        executor.setThreadNamePrefix("NenNipezScrapper-");
        executor.initialize();
        return executor;
    }
}
