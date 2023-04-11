package com.cydeo.service.impl;

import com.cydeo.enums.AccountType;
import com.cydeo.exception.AccountOwnershipException;
import com.cydeo.exception.BadRequestException;
import com.cydeo.exception.BalanceNotSufficientException;
import com.cydeo.exception.UnderConstructionException;
import com.cydeo.model.Account;
import com.cydeo.model.Transaction;
import com.cydeo.repository.AccountRepository;
import com.cydeo.repository.TransactionRepository;
import com.cydeo.service.TransactionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
public class TransactionServiceImpl implements TransactionService {

    @Value("${under_construction}")
    private boolean underConstruction;

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public TransactionServiceImpl(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public Transaction makeTransfer(Account sender, Account receiver, BigDecimal amount, Date creationDate, String message) {

        if (!underConstruction) {

            validateAccount(sender, receiver);
            checkAccountOwnership(sender, receiver);
            executeBalanceAndUpdateIfRequired(amount, sender, receiver);

            Transaction transaction = Transaction.builder().sender(sender.getId())
                    .receiver(receiver.getId()).amount(amount)
                    .creationDate(creationDate).message(message).build();

            return transactionRepository.save(transaction);
        } else throw new UnderConstructionException("App is under construction!");
    }

    //Validation methods
    private void executeBalanceAndUpdateIfRequired(BigDecimal amount, Account sender, Account receiver) {
        if (checkSenderBalance(sender, amount)) {

            sender.setBalance(sender.getBalance().subtract(amount));
            receiver.setBalance(receiver.getBalance().add(amount));

        } else {
            throw new BalanceNotSufficientException("Sender does not have enough funds to proceed!");
        }
    }

    private boolean checkSenderBalance(Account sender, BigDecimal amount) {
        return sender.getBalance().subtract(amount).compareTo(BigDecimal.ZERO) >= 0;
    }

    private void checkAccountOwnership(Account sender, Account receiver) {

        if ((sender.getAccountType().equals(AccountType.SAVING)
                || receiver.getAccountType().equals(AccountType.SAVING))
                && !receiver.getUserId().equals(sender.getUserId()))
            throw new AccountOwnershipException("Since you are using a saving account, the sender and receiver UserIds must be the same!");

    }

    private void validateAccount(Account sender, Account receiver) {

        if (sender == null || receiver == null) {
            throw new BadRequestException("Sender or Receiver cannot be null!");
        }

        if (sender.getId().equals(receiver.getId())) {
            throw new BadRequestException("Sender account needs to be different from Receiver!");
        }

        findAccountById(sender.getId());
        findAccountById(receiver.getId());

    }

    private void findAccountById(UUID id) {
        accountRepository.findById(id);
    }


    @Override
    public List<Transaction> findAllTransactions() {
        return transactionRepository.findAll();
    }

    @Override
    public List<Transaction> last10Transactions() {
        return transactionRepository.findLast10Transactions();
    }

    @Override
    public List<Transaction> findAllTransactionsById(UUID id) {
        return transactionRepository.findAllTransactionsByIdInRepo(id);
    }
}
