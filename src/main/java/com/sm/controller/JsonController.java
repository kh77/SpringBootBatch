package com.sm.controller;

import com.sm.config.json.JsonBatchConfiguration;
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
public class JsonController {

    @Autowired
    JobLauncher jobLauncher;

    @Autowired
    JsonBatchConfiguration configuration;

    @GetMapping("/json-job")
    public ResponseEntity<String> runJsonJob(){
        try {
            Map<String, JobParameter> map = new HashMap<>();
            map.put("fileInput",new JobParameter("input/product.json"));
            map.put("date",new JobParameter(new Date().getTime()));
            jobLauncher.run(configuration.jsonJob(), new JobParameters(map));
        } catch (Exception e) {
            return new ResponseEntity<String>("Failure: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<String>("json-job Success", HttpStatus.OK);
    }

}
