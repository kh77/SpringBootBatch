package com.sm.controller;

import com.sm.config.custom.CustomBatchConfiguration;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
public class CustomController {

    @Autowired
    JobLauncher jobLauncher;

    @Autowired
    CustomBatchConfiguration configuration;

    @GetMapping("/custom-job")
    public ResponseEntity<String> runCustomJob(){
        try {
            Map<String, JobParameter> map = new HashMap<>();
            map.put("date", new JobParameter(new Date().getTime()));
            jobLauncher.run(configuration.customJob(), new JobParameters(map));
        } catch (Exception e) {
                return new ResponseEntity<String>("Failure: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<String>("custom-job Success", HttpStatus.OK);

    }

}
