package com.examplewebflux.webflux.controllers;

import com.examplewebflux.webflux.models.dao.ProductDao;
import com.examplewebflux.webflux.models.documents.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/productos")
public class ProductoRestController {

    @Autowired
    private ProductDao productDao;

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    @GetMapping()
    public Flux<Product> index(){

        Flux<Product> products = productDao.findAll()
                .map(product -> {
                    product.setName(product.getName().toUpperCase());
                    return product;
                }).doOnNext(product -> log.info(product.getName()));
        return products;
    }

    @GetMapping("/{id}")
    public Mono<Product> showId(@PathVariable String id){

        //Mono<Product> products = productDao.findById(id) // simple method

        Flux<Product> products = productDao.findAll();
        Mono<Product> product = products.filter(product1 -> id.equals(product1.getId()))
                .next();// return only the first item in the flow

        return product;
    }




}
