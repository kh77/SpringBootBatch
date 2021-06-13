package com.sm.config.xml;

import com.sm.model.Product;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.oxm.xstream.XStreamMarshaller;

import java.util.HashMap;

@Configuration
public class XmlBatchConfiguration {

    @Autowired
    private JobBuilderFactory jobs;

    @Autowired
    private StepBuilderFactory steps;


    @StepScope
    @Bean
    public StaxEventItemReader xmlItemReader(@Value( "#{jobParameters['fileInput']}" )
                                                         FileSystemResource inputFile){
        // where to read the xml file
        StaxEventItemReader reader = new StaxEventItemReader();
        reader.setResource(inputFile);
        //need to let reader to know which tags describe the domain object
        reader.setFragmentRootElementName("product");

        // tell reader how to parse XML and which domain object to be mapped
        reader.setUnmarshaller(new Jaxb2Marshaller(){
            {
                setClassesToBeBound(Product.class);
            }
        });
        return reader;
    }

    @Bean
    @StepScope
    public StaxEventItemWriter xmlWriter(@Value("#{jobParameters['fileOutput']}" )FileSystemResource outputFile){
        XStreamMarshaller marshaller = new XStreamMarshaller();
        HashMap<String,Class> aliases = new HashMap<>();
        aliases.put("product",Product.class);
        marshaller.setAliases(aliases);
        marshaller.setAutodetectAnnotations(true);
        StaxEventItemWriter staxEventItemWriter = new StaxEventItemWriter();
        staxEventItemWriter.setResource(outputFile);
        staxEventItemWriter.setMarshaller(marshaller);
        // root element
        staxEventItemWriter.setRootTagName("Products");
        return staxEventItemWriter;
    }

    @Bean
    public Step xmlStep(){
        return steps.get("xmlStep").
                <Integer,Integer>chunk(3)
                .reader(xmlItemReader(null))
                //.writer(new ConsoleItemWriter())
                .writer(xmlWriter(null))
                .build();
    }

    @Bean
    public Job xmlJob(){
        return jobs.get("xmlJob")
                .incrementer(new RunIdIncrementer())
                .start(xmlStep())
                .build();
    }

}
