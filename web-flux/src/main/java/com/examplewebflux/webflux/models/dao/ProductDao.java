package com.examplewebflux.webflux.models.dao;


import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import com.examplewebflux.webflux.models.documents.Product;

public interface ProductDao  extends ReactiveMongoRepository<Product, String> {


}
