package com.sm.config;

import com.sm.batch.listener.HelloWorldJobExecutionListener;
import com.sm.batch.listener.HelloWorldStepExecutionListener;
import com.sm.batch.processor.InMemeItemProcessor;
import com.sm.batch.reader.InMemReader;
import com.sm.model.Product;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.FixedLengthTokenizer;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;

import javax.sql.DataSource;

//@Configuration
public class BatchConifguration {

    @Autowired
    private JobBuilderFactory jobs;

    @Autowired
    private StepBuilderFactory steps;

    @Autowired
    private HelloWorldJobExecutionListener helloWorldJobExecutionListener;

    @Autowired
    private HelloWorldStepExecutionListener helloWorldStepExecutionListener;

    @Autowired
    private InMemeItemProcessor inMemeItemProcessor;

    @Autowired
    private DataSource dataSource;

    @Bean
    public InMemReader reader(){
       return new InMemReader();
    }

    @StepScope
    @Bean
    public FlatFileItemReader flatfixFileItemReader(
            @Value( "#{jobParameters['fileInput']}" )
                    FileSystemResource inputFile ){
        FlatFileItemReader reader = new FlatFileItemReader();
        // step 1 let reader know where is the file
        reader.setResource( inputFile );

        //create the line Mapper
        reader.setLineMapper(
                new DefaultLineMapper<Product>(){
                    {
                        setLineTokenizer( new FixedLengthTokenizer() {
                            {
                                setNames( new String[]{"prodId","productName","productDesc","price","unit"});
                                setColumns(
                                        new Range(1,16),
                                        new Range(17,41),
                                        new Range(42,65),
                                        new Range(66, 73),
                                        new Range(74,80)

                                );
                            }
                        });

                        setFieldSetMapper( new BeanWrapperFieldSetMapper<Product>(){
                            {
                                setTargetType(Product.class);
                            }
                        });
                    }
                }

        );
        //step 3 tell reader to skip the header
        reader.setLinesToSkip(1);
        return reader;

    }
//
//    @Bean
//    public Job helloWorldJob(){
//        return jobs.get("helloWorldJob")
//                .incrementer(new RunIdIncrementer())
//                .listener(helloWorldJobExecutionListener)
//              //  .start(step1())
//
//                .build();
//    }
}
