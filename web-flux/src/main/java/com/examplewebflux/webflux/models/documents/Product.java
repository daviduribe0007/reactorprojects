package com.examplewebflux.webflux.models.documents;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@RequiredArgsConstructor
@Document(collection  = "products")
public class Product {

    @Id
    private String id;
    @NonNull
    private String name;
    @NonNull
    private Double price;
    private Date createAt;
}
