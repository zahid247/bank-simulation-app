package com.cydeo.controller;

import com.cydeo.model.Transaction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TransactionController {

    @GetMapping("/make-transfer")
    public String getMakeTransfer(Model model){

        model.addAttribute("transaction", Transaction.builder().build());

        return "transaction/make-transfer";
    }


}
