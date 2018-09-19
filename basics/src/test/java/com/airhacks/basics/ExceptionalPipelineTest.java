package com.airhacks.basics;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;
import org.junit.Before;
import org.junit.Test;

public class ExceptionalPipelineTest {


    @Before
    public void init() {

        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "5");

    }

    @Test
    public void forkJoinConfiguration() throws InterruptedException {

        Stream.iterate(0, i -> i + 1).limit(200).forEach( i -> {
            CompletableFuture.runAsync(this::slow);
        } );

        Thread.sleep(20000);
    }

    @Test
    public void forkJoinConfigurationWithCustom() throws InterruptedException {

        ExecutorService custom = Executors.newCachedThreadPool();

        Stream.iterate(0, i -> i + 1).limit(200).forEach( i -> {
            CompletableFuture.runAsync(this::slow, custom);
        } );

        Thread.sleep(20000);

    }



    void slow () {
        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void pipelineExceptional() throws ExecutionException, InterruptedException {

       CompletableFuture.supplyAsync(this::exceptional).exceptionally(this::transform).thenAccept(this::consumeMessage);

    }

    @Test
    public void pipelineExceptionalHandle() throws ExecutionException, InterruptedException {

        CompletableFuture.supplyAsync(this::exceptional).handle(this::handle).thenAccept(this::consumeMessage);

    }


    String handle(String valid, Throwable ex) {
        return  valid + ex;
    }


    void consumeMessage(String message) {
        System.out.println("message: " + message);
    }

    public String exceptional() {
        throw new IllegalStateException("happens");
    }

    String transform(Throwable t) {
        return  t.toString();
    }
}
