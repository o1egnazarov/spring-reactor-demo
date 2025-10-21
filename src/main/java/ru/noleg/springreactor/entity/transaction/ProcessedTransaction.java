package ru.noleg.springreactor.entity.transaction;

public record ProcessedTransaction(Transaction originalTx, Status status) {
    public enum Status {PENDING, SUCCESS, FAILED, SYSTEM_ERROR}
}
