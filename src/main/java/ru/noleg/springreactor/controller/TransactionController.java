package ru.noleg.springreactor.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import ru.noleg.springreactor.entity.transaction.ProcessedTransaction;
import ru.noleg.springreactor.service.transaction.TransactionGeneratorService;
import ru.noleg.springreactor.service.transaction.TransactionProcessorService;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionGeneratorService transactionService;
    private final TransactionProcessorService processor;

    public TransactionController(TransactionGeneratorService transactionService, TransactionProcessorService processor) {
        this.transactionService = transactionService;
        this.processor = processor;
    }

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ProcessedTransaction> streamTransactions() {
        return this.processor
                .processTransactions(this.transactionService.generateTransactions(1000));
    }
}