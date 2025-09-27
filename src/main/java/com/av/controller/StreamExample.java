package com.av.controller;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class StreamExample {
  public static void main(String[] args) {
    // Sample list
    List<String> names = Arrays.asList("Alice", "Bob", "Anand", "Charlie", "Amir");

    // Example usage of various methods
    List<String> result = names.stream().filter(n -> n.startsWith("A")) // filter
        .distinct() // remove duplicate
        .map(String::toUpperCase) // transform
        .sorted() // sort
        .peek(System.out::println) // debug
        .limit(3) // take first 3
        .collect(Collectors.toList()); // collect result

    System.out.println("Result: " + result);

    // Some terminal ops
    long count = names.stream().count();
    boolean any = names.stream().anyMatch(n -> n.startsWith("C"));
    Optional<String> min = names.stream().min(Comparator.comparing(String::length));

    System.out.println("Count = " + count);
    System.out.println("Any name starts with C? " + any);
    System.out.println("Shortest name: " + min.orElse("N/A"));
  }
}
