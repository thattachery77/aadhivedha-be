package com.av.controller;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@FunctionalInterface
interface SumCalculator {
  int sum(int a, int b);
}


public class Test {

  public static void main(String[] args) {
    SumCalculator calculator = (a, b) -> a + b;
    int result = calculator.sum(5, 7);
    System.out.println("Sum: " + result); // Output: Sum: 12


    Optional<String> name = Optional.of("John");
    name.ifPresent(System.out::println);

    ExecutorService service = Executors.newFixedThreadPool(3);
    service.submit(() -> System.out.println("Task executed"));
    service.shutdown();

    /*
     * try { Future<Integer> future = service.submit(() -> 10 + 20);
     * 
     * System.out.println(future.get()); } catch (InterruptedException | ExecutionException e) { //
     * TODO Auto-generated catch block // e.printStackTrace(); } // blocks until result is ready
     */


    Counter counter = new Counter();

    Thread t1 = new Thread(() -> {
      for (int i = 0; i < 1000; i++) {
        counter.increment();
      }
    });

    Thread t2 = new Thread(() -> {
      for (int i = 0; i < 1000; i++) {
        counter.increment();
      }
    });

    try {
      t1.start();
      t2.start();
      t1.join();
      t2.join();
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    System.out.println("Final count: " + counter.getCount());
  }

}


class Counter {
  private int count = 0;

  public synchronized void increment() {
    count++;
  }

  public int getCount() {
    return count;
  }
}
