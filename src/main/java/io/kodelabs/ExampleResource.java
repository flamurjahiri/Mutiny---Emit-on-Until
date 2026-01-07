package io.kodelabs;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class ExampleResource {

    public static void main(String[] args) {

        Function<AtomicInteger, Uni<? extends Integer>> fn = s -> {
            return Uni.createFrom().item(s.get()).onItem().invoke(i -> System.out.println("Receiving on init: " + s.getAndIncrement()));
        };


        Multi.createBy().repeating().uni(
                        () -> new AtomicInteger(0),
                        a -> Uni.createFrom().deferred(() -> fn.apply(a))
                ).until(i -> true)
                .collect()
                .last() // first
                .map(i -> {
                    System.out.println("First case : Received after init: " + i);
                    return i;
                }).subscribe().with(i -> System.out.println("Success"), f -> System.out.println(f.getMessage()));


        System.out.println("First case finished");
        System.out.println();
        System.out.println();

        AtomicInteger count = new AtomicInteger(0);

        Multi.createBy().repeating().uni(
                        () -> new AtomicInteger(5),
                        a -> Uni.createFrom().deferred(() -> fn.apply(a))
                ).until(i -> count.getAndIncrement() == 1)
                .collect()
                .last() // first
                .map(i -> {
                    System.out.println("First case : Received after init: " + i);
                    return i;
                }).subscribe().with(i -> System.out.println("Success"), f -> System.out.println(f.getMessage()));


    }
}
