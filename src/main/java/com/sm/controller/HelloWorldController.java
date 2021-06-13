package com.sm.controller;

import com.sm.config.dummy.HelloWorldConfiguration;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {

    @Autowired
    JobLauncher jobLauncher;

    @Autowired
    HelloWorldConfiguration configuration;

    @GetMapping("/hello-world-job")
    public ResponseEntity<String> runHelloWorldJob(){
        try {
            jobLauncher.run(configuration.helloWorldJob(), new JobParameters());
        } catch (Exception e) {
            return new ResponseEntity<String>("Failure: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<String>("helloworld-job Success", HttpStatus.OK);

    }

}
