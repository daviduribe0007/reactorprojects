package com.examplewebflux.webflux.controllers;

import com.examplewebflux.webflux.models.dao.ProductDao;
import com.examplewebflux.webflux.models.documents.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Locale;

@Controller
public class ProductController {

    @Autowired
    private ProductDao productDao;

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    @GetMapping({"List", "/"})
    public String list(Model model){
        Flux<Product> products = productDao.findAll()
                .map(product -> {
                    product.setName(product.getName().toUpperCase());
                            return product;
                });

        products.subscribe(product -> log.info(product.getName()));

        model.addAttribute("products", products);
        model.addAttribute("tittle","List of products");
        return "List";
    }

    @GetMapping("/Listdatadrive")
    public String listReactiveDataDrive(Model model){
        Flux<Product> products = productDao.findAll()
                .map(product -> {
                    product.setName(product.getName().toLowerCase(Locale.ROOT));
                    return product;
                })
                .delayElements(Duration.ofSeconds(1));

        products.subscribe(product -> log.info(product.getName()));

        model.addAttribute("products", new ReactiveDataDriverContextVariable(products, 2));
        model.addAttribute("tittle","List of products");
        return "List";
    }

    @GetMapping("/Listfull")
    public String listfull(Model model){//with spring.thymeleaf.reactive.max-chunk-size=50 on properties you can change the time to load
        Flux<Product> products = productDao.findAll()
                .map(product -> {
                    product.setName(product.getName().toUpperCase());
                    return product;
                }).repeat(5000);

        products.subscribe(product -> log.info(product.getName()));

        model.addAttribute("products", products);
        model.addAttribute("tittle","List of products");
        return "List";
    }

    @GetMapping("/list-chunck")
    public String listGhunked(Model model){
        Flux<Product> products = productDao.findAll()
                .map(product -> {
                    product.setName(product.getName().toUpperCase());
                    return product;
                }).repeat(5000);

        products.subscribe(product -> log.info(product.getName()));

        model.addAttribute("products", products);
        model.addAttribute("tittle","List of products");
        return "list-chunck";//this theturn to the view
    }



}
