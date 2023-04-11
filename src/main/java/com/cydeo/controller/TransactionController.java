package com.cydeo.controller;

import com.cydeo.model.Account;
import com.cydeo.model.Transaction;
import com.cydeo.service.AccountService;
import com.cydeo.service.TransactionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
public class TransactionController {

    private final AccountService accountService;
    private final TransactionService transactionService;

    public TransactionController(AccountService accountService, TransactionService transactionService) {
        this.accountService = accountService;
        this.transactionService = transactionService;
    }

    @GetMapping("/make-transfer")
    public String getMakeTransfer(Model model){

        model.addAttribute("transaction", Transaction.builder().build());
        model.addAttribute("accounts", accountService.listAllAccount());
        model.addAttribute("lastTransactions", transactionService.last10Transactions());


        return "transaction/make-transfer";
    }

    @PostMapping("/transfer")
    public String transfer(@ModelAttribute("transaction") Transaction transaction, Model model){

        Account sender = accountService.retrieveById(transaction.getSender());
        Account receiver = accountService.retrieveById(transaction.getReceiver());

        transactionService.makeTransfer(sender, receiver, transaction.getAmount(),
                new Date(), transaction.getMessage());

        return "redirect:/make-transfer";
    }

    @GetMapping("/transaction/{id}")
    public String showTransactions(@PathVariable("id")UUID id, Model model){

        model.addAttribute("transactionsById",
                transactionService.findAllTransactionsById(id));

        return "/transaction/transactions";
    }


}
