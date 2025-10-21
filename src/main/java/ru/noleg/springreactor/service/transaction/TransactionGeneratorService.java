package ru.noleg.springreactor.service.transaction;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import ru.noleg.springreactor.entity.transaction.Transaction;
import ru.noleg.springreactor.entity.transaction.TransactionType;

import java.time.Duration;
import java.util.Random;

@Service
public class TransactionGeneratorService {

    public Flux<Transaction> generateTransactions(long countOfTransaction) {
        return Flux
                .interval(Duration.ofSeconds(1))
                .map(tick -> getRandomTransaction())
                .take(countOfTransaction)
                .doOnComplete(() -> System.out.println("Поток транзакций остановлен"));
    }

    private Transaction getRandomTransaction() {
        var random = new Random();

        String randomUserId = "user" + random.nextInt(100);
        double randomAmount = Math.ceil(random.nextDouble() * 100);
        TransactionType randomType = random.nextBoolean() ? TransactionType.DEBIT : TransactionType.CREDIT;

        return new Transaction(randomUserId, randomAmount, randomType);
    }
}
