package ru.noleg.springreactor.service.transaction;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.noleg.springreactor.entity.transaction.ProcessedTransaction;
import ru.noleg.springreactor.entity.transaction.Transaction;
import ru.noleg.springreactor.entity.transaction.TransactionType;

import java.time.Duration;
import java.util.List;

@Service
public class TransactionProcessorService {
    private final TransactionGeneratorService transactionGeneratorService;

    public TransactionProcessorService(TransactionGeneratorService transactionGeneratorService) {
        this.transactionGeneratorService = transactionGeneratorService;
    }

    public Flux<ProcessedTransaction> processTransactions(Flux<Transaction> inputStream) {
        return inputStream
                // --- Заглядываем в последовательность ---
                .doOnNext(tx -> System.out.println("Получена транзакция: " + tx.getId()))
                .doOnError(error -> System.err.println("Ошибка в потоке: " + error.getMessage()))

                // --- Работаем со временем ---
                // Если транзакция не приходит в течение 2 секунд, кидаем ошибку
                .timeout(Duration.ofSeconds(2))

                // --- Фильтрация ---
                // Обрабатываем только дебетовые транзакции
                .filter(tx -> tx.getType() == TransactionType.DEBIT)
                // Игнорируем подозрительные транзакции на этом этапе
                .filter(tx -> !tx.isSuspicious())

                // --- Преобразование ---
                // 1. Простое преобразование с map
                .map(tx -> new ProcessedTransaction(tx, ProcessedTransaction.Status.PENDING))

                // 2. Асинхронное преобразование с flatMap (например, сохранение в БД)
                .flatMap(processedTx ->
                        saveToDatabase(processedTx)
                                // --- Обработка ошибок при сохранении ---
                                .onErrorResume(error -> {
                                    System.err.println("Не удалось сохранить: " + error.getMessage());
                                    // Продолжаем поток, возвращая транзакцию со статусом ERROR
                                    return Mono.just(new ProcessedTransaction(processedTx.originalTx(),
                                            ProcessedTransaction.Status.FAILED));
                                })
                )

                // --- Расщепление потока ---
                // Группируем по userId
                .groupBy(processedTx -> processedTx.originalTx().getUserId())

                // Берем первую группу (для user2) и работаем с ней
                .flatMap(group -> group
                        .buffer(Duration.ofSeconds(10)) // Накапливаем транзакции за 5 секунд
                        .flatMap(this::calculateBatchTotal) // Считаем сумму пачки
                )

                // --- Еще одна обработка ошибок для всего потока ---
                .onErrorReturn(new ProcessedTransaction(Transaction.createErrorTransaction(),
                        ProcessedTransaction.Status.SYSTEM_ERROR));
    }

    private Mono<ProcessedTransaction> saveToDatabase(ProcessedTransaction tx) {
        // Имитация асинхронного сохранения с возможной ошибкой
        if (Math.random() > 0.8) {
            return Mono.error(new RuntimeException("Ошибка базы данных"));
        }
        return Mono.just(tx);
    }

    private Flux<ProcessedTransaction> calculateBatchTotal(List<ProcessedTransaction> batch) {
        // Имитация расчета общей суммы для пачки транзакций
        double total = batch.stream()
                .mapToDouble(tx -> tx.originalTx().getAmount())
                .sum();
        System.out.println("Общая сумма для пачки из " + batch.size() +
                " транзакций: " + total);
        return Flux.fromIterable(batch);
    }
}