package com.cydeo.controller;


import com.cydeo.enums.AccountStatus;
import com.cydeo.enums.AccountType;
import com.cydeo.model.Account;
import com.cydeo.service.AccountService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Date;
import java.util.UUID;

@Controller
@AllArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/index")
    public String index(Model model){

        model.addAttribute("accountList", accountService.listAllAccount());

        return "account/index";
    }

    @GetMapping("/create-account")
    public String createAccount(Model model){

        model.addAttribute("account", Account.builder().build());
        model.addAttribute("accountTypes", AccountType.values());

        return "account/create-account";
    }

    @PostMapping("/create")
    public String createdAccount(@ModelAttribute("account") Account account){

//        System.out.println(account);
        accountService.createNewAccount(account.getBalance(), new Date(),
                account.getAccountType(), account.getUserId());

        return "redirect:/index";
    }

    @GetMapping("/delete/{id}")
    public String deleteAccount(@PathVariable("id")UUID id){
//        System.out.println(id);

        accountService.deleteAccount(id);

        return "redirect:/index";
    }

    @GetMapping("/activate/{id}")
    public String activateAccount(@PathVariable("id")UUID id, Model model){

        model.addAttribute("statuses", AccountStatus.values());

        accountService.activateAccount(id);

        return "redirect:/index";
    }



}
