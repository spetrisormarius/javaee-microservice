package com.airhacks.basics;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import org.junit.Test;

public class PipelineTest {

    String message()  {

        return "hey duke:" + System.currentTimeMillis();

    }

    void consumeMessage(String message) {
        System.out.println("message: " + message);
    }

    void finalAction() {
        System.out.println("Clean up!!!");
    }

    String  beautify(String input) {
        return " *** " + input + " *** ";
    }

    String  beautifyWithSleep(String input) {

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return " *** " + input + " *** ";
    }


    String greeting () {
        return "good morning";
    }

    String combinator(String first, String second) {
        return  first + " -- " + second;
    }


    @Test
    public void pipeline() {
        CompletableFuture.supplyAsync(this::message).thenApply(this::beautify).
                thenAccept(this::consumeMessage).thenRun(this::finalAction);
    }

    @Test
    public void combinePipeline() {

        CompletableFuture<String> first = CompletableFuture.supplyAsync(this::message);
        CompletableFuture<String> second = CompletableFuture.supplyAsync(this::greeting);

        first.thenCombine(second, this::combinator).thenApply(this::beautify).
                thenAccept(this::consumeMessage);
    }

    @Test
    public void combinePipelineWithSleep() throws ExecutionException, InterruptedException {

        CompletableFuture<String> first = CompletableFuture.supplyAsync(this::message).thenApplyAsync(this::beautifyWithSleep);
        CompletableFuture<String> second = CompletableFuture.supplyAsync(this::greeting).thenApplyAsync(this::beautifyWithSleep);

        first.thenCombine(second, this::combinator).
                thenAccept(this::consumeMessage).get();
    }


    @Test
    public void compose() {

        CompletableFuture.supplyAsync(this::message).
                thenCompose(this::compose).
                thenAccept(this::consumeMessage);

    }


    CompletionStage<String> compose(String input) {
        return CompletableFuture.supplyAsync( ()->input ).thenApply(this::beautifyWithSleep);
    }


}
