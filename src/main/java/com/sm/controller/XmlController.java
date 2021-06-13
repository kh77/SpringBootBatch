package com.sm.controller;

import com.sm.config.xml.XmlBatchConfiguration;
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
public class XmlController {

    @Autowired
    JobLauncher jobLauncher;

    @Autowired
    XmlBatchConfiguration configuration;

    @GetMapping("/xml-job")
    public ResponseEntity<String> runXmlJob(){
        try {
            Map<String, JobParameter> map = getStringJobParameterMap();
            jobLauncher.run(configuration.xmlJob(), new JobParameters(map));
        } catch (Exception e) {
            return new ResponseEntity<String>("Failure: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<String>("xml-job Success", HttpStatus.OK);
    }

    private Map<String, JobParameter> getStringJobParameterMap() {
        Map<String, JobParameter> map = new HashMap<>();
        map.put("fileInput",new JobParameter("input/product.xml"));
        map.put("date",new JobParameter(new Date().getTime()));
        map.put("fileOutput",new JobParameter("output/product_output.xml"));
        return map;
    }
}
