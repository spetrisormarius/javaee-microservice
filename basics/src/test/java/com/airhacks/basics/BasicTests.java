package com.airhacks.basics;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.junit.Test;


public class BasicTests {

    @Test
    public void references() {

        //Runnable runnable = ()-> System.out.println("hey duke");
        Runnable runnable = this::dipslay;

        new Thread(runnable).start();

    }

    void dipslay() {
        System.out.println("hey duke");
    }

    void dipslaySleep() {

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void threadsOutOfMemory() throws InterruptedException {
        List<Thread>  pool = new ArrayList<>();

        for (int i = 0; i < 10000; i++) {
            Runnable run = this::dipslaySleep;
            Thread thread = new Thread(run);
            pool.add(thread);
            thread.start();

            Thread.sleep(10);
        }

    }

    public String message() {
        return "hey duke " + System.currentTimeMillis();
    }

    @Test
    public void callable() throws ExecutionException, InterruptedException {

        Callable<String> callable = this::message;

        ExecutorService pool = Executors.newFixedThreadPool(2);

        List<Future<String>> futures = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            futures.add( pool.submit(callable) );
        }

        for ( Future<String> future: futures ) {
            String message = future.get();
            System.out.println("result= " + message);
        }
    }


    @Test
    public void backpressure() {

        BlockingQueue<Runnable> workQueue = new LinkedBlockingDeque<>(2);

//        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 1,
//                60, TimeUnit.SECONDS, workQueue, new ThreadPoolExecutor.CallerRunsPolicy());

//        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 1,
//                60, TimeUnit.SECONDS, workQueue, new ThreadPoolExecutor.AbortPolicy());


        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 1,
                60, TimeUnit.SECONDS, workQueue, this::rejectedExecution);


        long start = System.currentTimeMillis();
        threadPoolExecutor.submit( this::dipslaySleep);
        duration(start);

        threadPoolExecutor.submit( this::dipslaySleep);
        duration(start);

        threadPoolExecutor.submit( this::dipslaySleep);
        duration(start);

        threadPoolExecutor.submit( this::dipslaySleep);
        duration(start);

    }

    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        System.out.println(" reject :" +  executor.getActiveCount());
    }

    private void duration(long start) {

        System.out.println(" took:" + (System.currentTimeMillis() - start));
    }



    @Test
    public void threadsPool() throws InterruptedException {

        ExecutorService pool = Executors.newFixedThreadPool(2);

        for (int i = 0; i < 10000; i++) {
            Runnable run = this::dipslaySleep;
            pool.submit(run);
            Thread.sleep(10);
        }

    }

    @Test
    public void fizzbuzz() {

        for (int i = 0; i < 20; i ++) {
            if ( i % 15 == 0) {
                System.out.println("FizzBuzz");
            } else if ( i % 3 == 0) {
                System.out.println("Fizz");
            } else  if (i % 5 == 0) {
                System.out.println("Buzz");
            } else  {
                System.out.println(i);
            }
        }

    }


}
