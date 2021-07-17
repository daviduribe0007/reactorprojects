package com.springbootreactor.demo;


import models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class ReactorAppApplication implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(ReactorAppApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(ReactorAppApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
       // exampleIterator();
       // exampleFlatMap ();
        //exampleToString();
exampleFluxToMono();
    }

    public void exampleFluxToMono () {

        log.info("--------------- Head flow -------------- ");
        List<User> names3List = new ArrayList<>();
        names3List.add( new User("Danilo","perez"));
        names3List.add(new User("camilo "," ruiz"));
        names3List.add(new User("David ","uribe"));
        names3List.add(new User("Antonio"," rios"));
        names3List.add(new User("Carmen"," lee"));
        names3List.add(new User("bruce","willis"));
        names3List.add(new User("bruce"," lee"));

        Flux.fromIterable(names3List)
                .collectList()
                .subscribe(list ->{
                    log.info(list.toString());//this show me the complete list
                    list.forEach(user -> log.info(user.toString()));//this interact all list and show me all objects but separated on items

                });


    }

    public void exampleFlatMap () {

        log.info("--------------- Head flow -------------- ");
        List<String> names3List = new ArrayList<>();
        names3List.add("Danilo perez");
        names3List.add("camilo ruiz");
        names3List.add("David uribe");
        names3List.add("Antonio rios");
        names3List.add("Carmen lee");
        names3List.add("bruce willis");
        names3List.add("bruce lee");

        Flux.fromIterable(names3List)
                .map(name -> new User(name.split(" ")[0], name.split(" ")[1]))
                .flatMap(user -> {
                    if (user.getName().equals("bruce")){
                        return Mono.just(user);
                    }
                    else{
                        return Mono.empty();//not emit nothing
                    }
                })
                .map(user -> {
                    String name = user.getName().toUpperCase();
                    user.setName(name);
                    return user;
                })
                .subscribe(user ->log.info(user.toString()));


    }

    public void exampleToString () {

        log.info("--------------- Head flow -------------- ");
        List<User> names3List = new ArrayList<>();
        names3List.add( new User("Danilo","perez"));
        names3List.add(new User("camilo "," ruiz"));
        names3List.add(new User("David ","uribe"));
        names3List.add(new User("Antonio"," rios"));
        names3List.add(new User("Carmen"," lee"));
        names3List.add(new User("bruce","willis"));
        names3List.add(new User("bruce"," lee"));

        Flux.fromIterable(names3List)
                .map(user ->  user.getName().toUpperCase().concat(" ").concat( user.getLastName()))
                .flatMap(name -> {
                    if (name.contains("bruce".toUpperCase())){//this showme when the object containt something text
                        return Mono.just(name);
                    }
                    else{
                        return Mono.empty();//not emit nothing
                    }
                })
                .map(name -> {
                    return name;
                })
                .subscribe(user ->log.info(user.toString()));


    }

    public void exampleIterator()  throws Exception {

        log.info("--------------- Head flow -------------- ");
        List<String> names3List = new ArrayList<>();
        names3List.add("Danilo perez");
        names3List.add("camilo ruiz");

        Flux<String> names3ToFlux = Flux.fromIterable(names3List);

        Flux<User> fNames = names3ToFlux.map(nombre -> new User(nombre.split(" ")[0], nombre.split(" ")[1]))
                .map(user -> {
                    String name = user.getName().toUpperCase();
                    user.setName(name);
                    return user;
                });

        fNames.subscribe(e -> log.info(e.toString()));

        log.info("------------------- first flow ---------------------");

        //All flows are immutable  because you can't change the flow you ccan make others flow from the start flow but
        // you never can change that flow
        Flux<String> names = Flux.just("David uribe", "Antonio rios", "Carmen lee","bruce lee","bruce willis", " ",
                "luis f");
        Flux<User> namesFiltered = names.map(nombre -> new User(nombre.split(" ")[0], nombre.split(" ")[1]))//this modified the
                // flux type String to flux type user
                .filter(user -> user.getName().equals("bruce"))// filter are used when you need filter something
                // for x characteristic
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
                new Runnable() { //thre are used when you need show something after execute the flow
                    @Override
                    public void run() {
                        log.info("The execution are finished");
                    }
                });

        log.info("-------- second flow -----");
        namesFiltered.subscribe(e -> log.info(e.toString()),
                error -> log.error(error.getMessage()),
                new Runnable() { //thre are used when you need show something after execute the flow
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
