package com.sm.controller;

import com.sm.config.file.FileBatchConfiguration;
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
public class FileController {

    @Autowired
    JobLauncher jobLauncher;

    @Autowired
    FileBatchConfiguration configuration;

    @GetMapping("/file-job")
    public ResponseEntity<String> runFileJob(){
        try {
            Map<String, JobParameter> map = getStringJobParameterMap();
            jobLauncher.run(configuration.fileJob(), new JobParameters(map));
        } catch (Exception e) {
            return new ResponseEntity<String>("Failure: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<String>("file-job Success", HttpStatus.OK);
    }

    private Map<String, JobParameter> getStringJobParameterMap() {
        Map<String, JobParameter> map = new HashMap<>();
        map.put("fileInput",new JobParameter("input/product.csv"));
        map.put("date",new JobParameter(new Date().getTime()));
        map.put("fileOutput",new JobParameter("output/product_output.csv"));
        return map;
    }

}
