package com.springbootreactor.demo;


import models.Comment;
import models.User;
import models.UserWithComment;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.sql.Time;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

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
        //exampleFluxToMono();
        // exampleUserWithComments();
        // exampleUserWithCommentsUsingWithZipWith();
        // exampleUserWithCommentsUsingWithZipWithForm2();
        //exampleUserWithZipWithAndRange();
        //exampleInterval();
        //delayElements();
        //delayElementsMethod();
       // infiniteInterval();
        //exampleIntervalSinceCreate();
        //exampleWithBackPressure();
        exampleWithBackPressure2();

    }

    public void exampleWithBackPressure(){

        Flux.range(1,10)
                .log() // this is used to see all trace on the route
                //.subscribe(integer -> log.info(integer.toString()));
                .subscribe(new Subscriber<Integer>() {

                    private Subscription s;
                    private  Integer limit = 2;
                    private  Integer consumed = 0;

                    @Override
                    public void onSubscribe(Subscription subscription) {
                        this.s = subscription;
                        //s.request(Long.MAX_VALUE);// this sent tha maximum elements
                        s.request(limit);

                    }

                    @Override
                    public void onNext(Integer integer) {
                        log.info(integer.toString());
                        consumed++;
                        if(consumed == limit){
                            consumed = 0;
                            s.request(limit);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void exampleWithBackPressure2(){

        Flux.range(1,10)
                .log() // this is used to see all trace on the route
                //.subscribe(integer -> log.info(integer.toString()));
                .limitRate(3)
                .subscribe(integer -> log.info(integer.toString()));
    }


    public void exampleIntervalSinceCreate() {
        Flux.create(emmiter -> {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() { // the first  position is the task 2 delay and 3 period
                private Integer counter = 0;
                @Override
                public void run() {
                    emmiter.next(++counter);
                    if(counter == 10){
                        timer.cancel();
                        emmiter.complete();
                    }
                    if(counter > 11){
                        timer.cancel();
                        emmiter.error(new InterruptedException("Error, The number cant exceeded the number 10 "));
                    }
                }
            }, 1000, 1000);
        })
                //.doOnNext(o -> log.info(o.toString()))
                .doOnComplete(()-> log.info("The flow finish at 10"))
                .subscribe(o -> log.info(o.toString()),
                        error -> log.error(error.getMessage()),
                        () -> log.info("The flow finish at 10"));
    }


    public void infiniteInterval() throws InterruptedException {

        CountDownLatch latch = new CountDownLatch(1);

        Flux.interval(Duration.ofSeconds(1))
                .doOnTerminate(latch::countDown)
                .flatMap(aLong -> {
                    if (aLong > 10) {
                        return Flux.error(new InterruptedException("The numbers only incremet to 10"));
                    }
                    return Flux.just(aLong);
                })
                .map(aLong -> "hi:" + aLong)
                .retry(2)//this are used when you need repeat something x times
                .subscribe(s -> log.info(s.toString()), e -> log.error(e.getMessage()));

        latch.await();

    }

    public void delayElementsMethod() throws InterruptedException {
        Flux<Integer> rangeFlux = Flux.range(500, 7)
                .delayElements(Duration.ofSeconds(1))
                .doOnNext(integer -> log.info(integer.toString()));

        rangeFlux.blockLast();

        Thread.sleep(8000);//this is other form to introduce intervals but there is not used actually

    }

    public void delayElements() {
        Flux<Integer> rangeFlux = Flux.range(1, 10);

        rangeFlux.delayElements(Duration.ofSeconds(1))
                .doOnNext(integer -> log.info(integer.toString()))
                .blockLast();

        rangeFlux.subscribe();

    }


    public void exampleInterval() {
        Flux<Integer> rangeFlux = Flux.range(10, 10);
        Flux<Long> delay = Flux.interval(Duration.ofSeconds(1));

        rangeFlux.zipWith(delay, (range, delayt) -> range)
                .doOnNext(integer -> log.info(integer.toString()))
                .blockLast();//Subscribe on the flow but block to the final element in that flow
    }

    public void exampleUserWithZipWithAndRange() {
        Flux<Integer> rangeFlux = Flux.range(10, 4);
        Flux.just(1, 2, 3, 4)
                .map(integer -> integer * 2)
                .zipWith(rangeFlux, (fluxOne, fluxTwo) ->
                        String.format("Primer flux : %d, Segundo flux : %d ", fluxOne, fluxTwo))
                .subscribe(s -> log.info(s));

    }


    public void exampleUserWithCommentsUsingWithZipWithForm2() {

        log.info("--------------- Head flow -------------- ");
        Mono<User> userMono = Mono.fromCallable(() -> new User("bruce", " lee"));
        Mono<Comment> commentMono = Mono.fromCallable(() -> {
            Comment comments = new Comment();
            comments.setComments("Hi");
            comments.setComments(" My name is roberto");
            comments.setComments(" What happen my doggy");
            return comments;
        });

        Mono<UserWithComment> userWithCommentMono = userMono.zipWith(commentMono)
                .map(tuple -> {
                    User u = tuple.getT1();
                    Comment c = tuple.getT2();
                    return new UserWithComment(u, c);
                });
        userWithCommentMono.subscribe(userWithComment -> log.info(userWithComment.toString()));
    }

    public void exampleUserWithCommentsUsingWithZipWith() {

        log.info("--------------- Head flow -------------- ");
        Mono<User> userMono = Mono.fromCallable(() -> new User("bruce", " lee"));
        Mono<Comment> commentMono = Mono.fromCallable(() -> {
            Comment comments = new Comment();
            comments.setComments("Hi");
            comments.setComments(" My name is roberto");
            comments.setComments(" What happen my doggy");
            return comments;
        });

        Mono<UserWithComment> userWithCommentMono = userMono.zipWith(commentMono, (user, Comment) -> new UserWithComment(user, Comment));
        userWithCommentMono.subscribe(userWithComment -> log.info(userWithComment.toString()));
    }

    public void exampleUserWithComments() {

        log.info("--------------- Head flow -------------- ");
        Mono<User> userMono = Mono.fromCallable(() -> new User("bruce", " lee"));
        Mono<Comment> commentMono = Mono.fromCallable(() -> {
            Comment comments = new Comment();
            comments.setComments("Hi");
            comments.setComments(" My name is roberto");
            comments.setComments(" My name is roberto");
            return comments;
        });

        userMono.flatMap(user -> commentMono
                .map(comment -> new UserWithComment(user, comment)))
                .subscribe(userWithComment -> log.info(userWithComment.toString()));
    }

    public void exampleFluxToMono() {

        log.info("--------------- Head flow -------------- ");
        List<User> names3List = new ArrayList<>();
        names3List.add(new User("Danilo", "perez"));
        names3List.add(new User("camilo ", " ruiz"));
        names3List.add(new User("David ", "uribe"));
        names3List.add(new User("Antonio", " rios"));
        names3List.add(new User("Carmen", " lee"));
        names3List.add(new User("bruce", "willis"));
        names3List.add(new User("bruce", " lee"));

        Flux.fromIterable(names3List)
                .collectList()
                .subscribe(list -> {
                    log.info(list.toString());//this show me the complete list
                    list.forEach(user -> log.info(user.toString()));//this interact all list and show me all objects but separated on items

                });


    }

    public void exampleFlatMap() {

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
                    if (user.getName().equals("bruce")) {
                        return Mono.just(user);
                    } else {
                        return Mono.empty();//not emit nothing
                    }
                })
                .map(user -> {
                    String name = user.getName().toUpperCase();
                    user.setName(name);
                    return user;
                })
                .subscribe(user -> log.info(user.toString()));


    }

    public void exampleToString() {

        log.info("--------------- Head flow -------------- ");
        List<User> names3List = new ArrayList<>();
        names3List.add(new User("Danilo", "perez"));
        names3List.add(new User("camilo ", " ruiz"));
        names3List.add(new User("David ", "uribe"));
        names3List.add(new User("Antonio", " rios"));
        names3List.add(new User("Carmen", " lee"));
        names3List.add(new User("bruce", "willis"));
        names3List.add(new User("bruce", " lee"));

        Flux.fromIterable(names3List)
                .map(user -> user.getName().toUpperCase().concat(" ").concat(user.getLastName()))
                .flatMap(name -> {
                    if (name.contains("bruce".toUpperCase())) {//this showme when the object containt something text
                        return Mono.just(name);
                    } else {
                        return Mono.empty();//not emit nothing
                    }
                })
                .map(name -> {
                    return name;
                })
                .subscribe(user -> log.info(user.toString()));


    }

    public void exampleIterator() throws Exception {

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
        Flux<String> names = Flux.just("David uribe", "Antonio rios", "Carmen lee", "bruce lee", "bruce willis", " ",
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
