package com.example.asyncresource;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class AsyncResourceController {

  private final static String HOST = "http://localhost";

  private String uploadSpreadsheetsEndpoint;
  private String processEndpoint;

  BlockingQueue<Runnable> workQueue = new LinkedBlockingDeque<>(Integer.MAX_VALUE);
  ExecutorService executorService;

  ExecutorService executorServiceCpu;

  @Autowired
  private RestTemplate restTemplate;

  private ScheduledThreadPoolExecutor delayer;

  @PostConstruct
  public void setUp() {
    uploadSpreadsheetsEndpoint = HOST + ":8080/messages";
    processEndpoint = HOST + ":8070/processor/beautification";

    executorService = new ThreadPoolExecutor(0, 2,
        60, TimeUnit.SECONDS, workQueue, new ThreadPoolExecutor.CallerRunsPolicy());

    executorServiceCpu = new ThreadPoolExecutor(0, 2,
        60, TimeUnit.SECONDS, workQueue, new ThreadPoolExecutor.CallerRunsPolicy());

    delayer = new ScheduledThreadPoolExecutor(2,
        (r, executor) -> {
          throw new RejectedExecutionException();
        });
  }





  @GetMapping(value = "/orchestration")
  public CompletableFuture<String> processMessage() {
    Supplier<String> mesageSupplier = () ->
        this.restTemplate.getForObject(uploadSpreadsheetsEndpoint, String.class);

    CompletableFuture<String> future = CompletableFuture.supplyAsync(mesageSupplier, executorService).
        thenApply(this::process).exceptionally(this::handle)
        //thenAccept(this::consume);
        //thenApply(this::consume)
        .applyToEither(timeoutAfter(1000L, TimeUnit.MICROSECONDS), this::consume);

    return future;
  }

  public <T> CompletableFuture<T> timeoutAfter(long timeout, TimeUnit unit) {
    CompletableFuture<T> result = new CompletableFuture<>();
    delayer.schedule(() -> result.completeExceptionally(new TimeoutException()), timeout, unit);
    return result;
  }

  private String handle(Throwable throwable) {
    return "overloaded " + throwable.getMessage();
  }

  String consume(String message) {

    this.restTemplate.postForObject(uploadSpreadsheetsEndpoint, message, String.class);

    System.out.println("message= " + message);

    return message;
  }

  String process( String message) {

    return this.restTemplate.postForObject(processEndpoint, message, String.class);
  }

  @GetMapping
  public  CompletableFuture<String> getMessage() {
        return CompletableFuture.supplyAsync(this::doSomeWork, executorService);
  }

  private String doSomeWork() {

    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    return "+ in the background: " + System.currentTimeMillis();
  }

}
