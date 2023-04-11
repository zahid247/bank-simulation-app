package com.cydeo.repository;

import com.cydeo.model.Transaction;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class TransactionRepository {

    public static List<Transaction> transactionList = new ArrayList<>();

    public Transaction save(Transaction transaction){
        transactionList.add(transaction);
        return transaction;
    }

    public List<Transaction> findAll(){
        return transactionList;
    }

    public List<Transaction> findLast10Transactions() {
        return transactionList.stream()
                .sorted(Comparator.comparing(Transaction ::getCreationDate).reversed())
                .limit(10)
                .collect(Collectors.toList());
    }

    public List<Transaction> findAllTransactionsByIdInRepo(UUID id) {
        return transactionList.stream()
                .filter(transaction -> transaction.getSender().equals(id) ||
                        transaction.getReceiver().equals(id))
                .collect(Collectors.toList());
    }
}
