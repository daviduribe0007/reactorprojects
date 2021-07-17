package com.springbootreactor.demo;


import com.springbootreactor.demo.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Flux;

import java.util.Locale;

@SpringBootApplication
public class ReactorAppApplication implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(ReactorAppApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(ReactorAppApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Flux<User> names = Flux.just("David", "Antonio", "Carmen", "", "luis")
                .map(nombre -> new User(nombre, null))//this modified the String flux to flux type user
                .doOnNext(user -> {
                    validateName(user.getName());
                })
                .map(user -> {
                    String name = user.getName().toUpperCase();
                    user.setName(name);
                    return user;
                });

        names.subscribe(e -> log.info(e.toString()),
                error -> log.error(error.getMessage()),
                new Runnable() {
                    @Override
                    public void run() {
                        log.info("The execution are finished");
                    }
                });

    }

    private void validateName(String s) {
        if (s.isEmpty() || s.equals(null)) {
            throw new RuntimeException("the name cant be empty or null");
        } else {
            log.info(s);
        }
    }
}
