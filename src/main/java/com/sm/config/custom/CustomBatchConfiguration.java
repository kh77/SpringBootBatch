package com.sm.config.custom;

import com.sm.batch.processor.InMemeItemProcessor;
import com.sm.batch.reader.InMemReader;
import com.sm.batch.writer.ConsoleItemWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomBatchConfiguration {

    @Autowired
    private JobBuilderFactory jobs;

    @Autowired
    private StepBuilderFactory steps;

    @Autowired
    private InMemeItemProcessor inMemeItemProcessor;

    @Bean
    public InMemReader reader(){
        return new InMemReader();
    }

    @Bean
    public Step customStep(){
        return steps.get("customStep").
                <Integer,Integer>chunk(3)
                .reader(reader())
                .processor(inMemeItemProcessor)
                .writer(new ConsoleItemWriter())
                .build();
    }

    @Bean
    public Job customJob(){
        return jobs.get("customJob")
                .incrementer(new RunIdIncrementer())
                .start(customStep())
                .build();
    }
}
