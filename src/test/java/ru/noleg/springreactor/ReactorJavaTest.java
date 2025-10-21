package ru.noleg.springreactor;


import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import ru.noleg.springreactor.entity.transaction.Transaction;
import ru.noleg.springreactor.entity.transaction.TransactionType;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public class ReactorJavaTest {

    @Test
    void simpleFluxExample() {
        Stream<String> stringStream = Stream.of("red", "green", "pirple", "red");

        Flux<String> fluxColors = Flux
                .fromStream(stringStream)
                .map(String::toUpperCase)
                .filter(s -> s.contains("RED"))
                .distinct();

        fluxColors.log().subscribe(System.out::println);
    }

    @Test
    void generateRandomTransaction() throws InterruptedException {
        List<Transaction> transactions = List.of(
                new Transaction("user1", 10.0, TransactionType.DEBIT),
                new Transaction("user2", 20.0, TransactionType.CREDIT),
                new Transaction("user3", 30.0, TransactionType.DEBIT),
                new Transaction("user4", 40.0, TransactionType.CREDIT),
                new Transaction("user5", 50.0, TransactionType.DEBIT),
                new Transaction("user6", 60.0, TransactionType.CREDIT),
                new Transaction("user7", 70.0, TransactionType.DEBIT),
                new Transaction("user8", 80.0, TransactionType.CREDIT),
                new Transaction("user9", 90.0, TransactionType.DEBIT),
                new Transaction("user10", 100.0, TransactionType.CREDIT)
        );

        Flux<Transaction> transactionFlux = Flux
                .fromIterable(transactions)
                .concatWith(Flux.interval(Duration.ofSeconds(1))
                        .map(tick -> generateTransactions()))
                .take(14)
                .doOnComplete(() -> System.out.println("Поток транзакций остановлен"));

        transactionFlux.subscribe(System.out::println);

        Thread.sleep(5000);
    }

    public Transaction generateTransactions() {
        Random random = new Random();

        String randomUserId = "user" + random.nextInt(100);
        double randomAmount = random.nextDouble() * 100;
        TransactionType randomType = random.nextBoolean() ? TransactionType.DEBIT : TransactionType.CREDIT;

        return new Transaction(randomUserId, randomAmount, randomType);
    }

    @Test
    void simpleMonoExample() {
        Flux.fromArray(new String[]{"red", "green", "blue"})
                .map(elem -> elem + "_array")
                .flatMap(elem -> Flux.just(elem.split("")))
                .subscribe(System.out::print);
    }

    @Test
    void zipExample() {

        Flux<String> fluxFruits = Flux.just("apple", "pear", "plum");
        Flux<String> fluxColors = Flux.just("red", "green", "blue");
        Flux<Integer> fluxAmounts = Flux.just(10, 20, 30);

        Flux
                .zip(fluxFruits, fluxColors, fluxAmounts)
                .subscribe(System.out::println);
    }

    @Test
    public void onErrorExample() {
        Flux<String> fluxCalc = Flux
                .just(-1, 0, 1)
                .map(i -> "10 / " + i + " = " + (10 / i));

        fluxCalc.subscribe(value -> System.out.println("Next: " + value),
                error -> System.err.println("Error: " + error));

    }

    @Test
    public void onErrorReturnExample() {
        Flux<String> fluxCalc = Flux.just(-1, 0, 1)
                .map(i -> "10 / " + i + " = " + (10 / i))
                .onErrorReturn(ArithmeticException.class, "Division by 0 not allowed");

        fluxCalc.subscribe(value -> System.out.println("Next: " + value),
                error -> System.err.println("Error: " + error));

    }
}
