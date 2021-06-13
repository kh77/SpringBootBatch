package com.sm.config.json;

import com.sm.batch.writer.ConsoleItemWriter;
import com.sm.model.Product;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

@Configuration
public class JsonBatchConfiguration {

    @Autowired
    private JobBuilderFactory jobs;

    @Autowired
    private StepBuilderFactory steps;


    @StepScope
    @Bean
    public JsonItemReader jsonItemReader(
            @Value( "#{jobParameters['fileInput']}" )
                    FileSystemResource inputFile){
        JsonItemReader reader = new JsonItemReader(inputFile, new JacksonJsonObjectReader(Product.class));
        return reader;
    }

    @Bean
    public Step jsonStep(){
        return steps.get("jsonStep").
                <Integer,Integer>chunk(3)
                .reader(jsonItemReader(null))
                .writer(new ConsoleItemWriter())
                .build();
    }

    @Bean
    public Job jsonJob(){
        return jobs.get("jsonJob")
                .incrementer(new RunIdIncrementer())
                .start(jsonStep())
                .build();
    }
}
