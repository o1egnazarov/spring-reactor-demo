package ru.noleg.springreactor.entity.transaction;

import java.time.LocalDateTime;
import java.util.UUID;

public class Transaction {
    private String id;
    private String userId;
    private double amount;
    private TransactionType type;
    private LocalDateTime createdAt;
    private boolean isSuspicious = false;

    public Transaction(String userId, double amount, TransactionType type) {
        this.id = UUID.randomUUID().toString();
        this.userId = userId;
        this.amount = amount;
        this.type = type;
        this.createdAt = LocalDateTime.now();
    }

    public static Transaction createErrorTransaction() {
        Transaction t = new Transaction("error-user", 0.0, TransactionType.DEBIT);
        t.isSuspicious = true;
        return t;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", amount=" + amount +
                ", type=" + type +
                ", createdAt=" + createdAt +
                ", isSuspicious=" + isSuspicious +
                '}';
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public double getAmount() {
        return amount;
    }

    public TransactionType getType() {
        return type;
    }

    public boolean isSuspicious() {
        return isSuspicious;
    }
}