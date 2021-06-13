package com.sm.config.file;

import com.sm.model.Product;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.file.FlatFileFooterCallback;
import org.springframework.batch.item.file.FlatFileHeaderCallback;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Configuration
public class ErrorFileBatchConfiguration {

    @Autowired
    private JobBuilderFactory jobs;

    @Autowired
    private StepBuilderFactory steps;

    @StepScope
    @Bean
    public FlatFileItemReader errorFlatFileItemReader(@Value( "#{jobParameters['fileInput']}" )
                                                             FileSystemResource inputFile ){
        FlatFileItemReader reader = new FlatFileItemReader();
        // step 1 let reader know where is the file
        reader.setResource( inputFile );

        //create the line Mapper
        reader.setLineMapper(
                new DefaultLineMapper<Product>(){
                    {
                        setLineTokenizer( new DelimitedLineTokenizer() {
                            {
                                setNames( new String[]{"productID","productName","productDesc","price","unit"});
                                setDelimiter(",");
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

    @Bean
    @StepScope
    public FlatFileItemWriter errorFlatFileItemWriter(@Value("#{jobParameters['fileOutput']}")
                                                             FileSystemResource outputFile) {

        FlatFileItemWriter writer = new FlatFileItemWriter<Product>(){
            @Override
            public String doWrite(List<? extends Product> items) {
                for(Product p :items){
                    if (p.getProductId() == 9 )
                        throw new RuntimeException("Because ID is 9");
                }
                return super.doWrite(items);
            }
        };

        writer.setResource(outputFile);
        writer.setLineAggregator( new DelimitedLineAggregator(){
            {
                setDelimiter("|");
                setFieldExtractor(new BeanWrapperFieldExtractor(){
                    {
                        setNames(new String[]{"productId","productName","productDesc","price","unit" });
                    }
                });
            }
        });

        // write header in the file
        writer.setHeaderCallback(new FlatFileHeaderCallback() {
            @Override
            public void writeHeader(Writer writer) throws IOException {
                writer.write("productID,productName,productDesc,price,unit");
            }
        });

        writer.setAppendAllowed(false);

        writer.setFooterCallback(new FlatFileFooterCallback() {
            @Override
            public void writeFooter(Writer writer) throws IOException {
                writer.write(" The file is created at " + new SimpleDateFormat().format(new Date()));
            }
        });
        return writer;
    }

    @Bean
    public Step errorFileStep(){
        return steps.get("errorFileStep").
                <Integer,Integer>chunk(3)
                .reader(errorFlatFileItemReader( null ))
                .writer(errorFlatFileItemWriter(null))
                .build();
    }

    @Bean
    public Job errorFileJob(){
        return jobs.get("errorFileJob")
                .incrementer(new RunIdIncrementer())
                .start(errorFileStep())
                .build();
    }

}
