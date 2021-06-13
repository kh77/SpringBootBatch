package com.sm.config.db;

import com.sm.batch.writer.ConsoleItemWriter;
import com.sm.model.Product;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import javax.sql.DataSource;

@Configuration
public class DatabaseBatchConfiguration {

    @Autowired
    private JobBuilderFactory jobs;

    @Autowired
    private StepBuilderFactory steps;

    @Autowired
    private DataSource dataSource;

    @Bean
    public JdbcCursorItemReader jdbcCursorItemReader(){
        JdbcCursorItemReader reader = new JdbcCursorItemReader();
        reader.setDataSource(this.dataSource);
        reader.setSql("select product_id, product_name, product_desc as productDesc, unit, price from products");
        reader.setRowMapper(new BeanPropertyRowMapper(){
            {
                setMappedClass(Product.class);
            }
        });
        return reader;
    }



    @Bean
    public Step DBStep(){
        return steps.get("DBStep").
                <Integer,Integer>chunk(3)
                .reader(jdbcCursorItemReader())
                .writer(new ConsoleItemWriter())
                .build();
    }

    @Bean
    public Job DBJob(){
        return jobs.get("DBJob")
                .incrementer(new RunIdIncrementer())
                .start(DBStep())
                .build();
    }
}
