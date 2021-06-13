package com.sm.service;

import com.sm.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;

@Service
public class ProductService {

    @Autowired
    RestTemplate restTemplate;

    public ArrayList<Product> getProducts(){

        String url="http://localhost:8080/products";
        Product[] products = restTemplate.getForObject(url,Product[].class);
        ArrayList<Product> productList = new ArrayList<Product>();
        for(Product p : products)
            productList.add(p);

        return productList;

    }
}
