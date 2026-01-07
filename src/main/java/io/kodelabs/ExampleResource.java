package io.kodelabs;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;

public class ExampleResource {

    public static void main(String[] args) {

        BiFunction<String, AtomicInteger, Uni<? extends Integer>> fn = (st, at) -> {
            return Uni.createFrom().item(at.get()).onItem().invoke(i -> System.out.println(st + " Receiving on init: " + at.getAndIncrement()));
        };


        Multi.createBy().repeating().uni(
                        () -> new AtomicInteger(0),
                        a -> Uni.createFrom().deferred(() -> fn.apply("FIRST", a))
                ).until(i -> true)
                .collect()
                .last() // first
                .map(i -> {
                    System.out.println("First: Received after init: " + i);
                    return i;
                }).subscribe().with(i -> {
                }, f -> System.out.println(f.getMessage()));


        try {
            Thread.sleep(20);
        } catch (Exception ign) {

        }

        System.out.println();
        System.out.println();


        AtomicInteger count = new AtomicInteger(0);

        Multi.createBy().repeating().uni(
                        () -> new AtomicInteger(5),
                        a -> Uni.createFrom().deferred(() -> fn.apply("SECOND", a))
                ).until(i -> count.getAndIncrement() == 1)
                .collect()
                .last() // first
                .map(i -> {
                    System.out.println("Second: Received after init: " + i);
                    return i;
                }).subscribe().with(i -> {
                }, f -> System.out.println(f.getMessage()));


        try {
            Thread.sleep(20);
        } catch (Exception ign) {

        }

        System.out.println();
        System.out.println();


        Multi.createBy().repeating().uni(
                        () -> new AtomicInteger(17),
                        a -> Uni.createFrom().deferred(() -> fn.apply("THIRD", a))
                ).whilst(i -> false)
                .collect()
                .last() // first
                .map(i -> {
                    System.out.println("Third: Received after init: " + i);
                    return i;
                }).subscribe().with(i -> {
                }, f -> System.out.println(f.getMessage()));

    }
}
