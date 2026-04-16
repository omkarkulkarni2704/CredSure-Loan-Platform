package com.credsure.controller;

import com.credsure.entity.BankOffer;
import com.credsure.entity.LoanApplication;
import com.credsure.repository.LoanApplicationRepository;
import com.credsure.service.BankOfferService;
import com.credsure.service.LoanService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LoanController {

    @Autowired
    private LoanApplicationRepository loanApplicationRepository;

    private final LoanService loanService;
    private final BankOfferService bankOfferService;

    public LoanController(LoanService loanService, BankOfferService bankOfferService) {
        this.loanService = loanService;
        this.bankOfferService = bankOfferService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("loanApplication", new LoanApplication());
        return "index";
    }

    @GetMapping("/admin")
    public String adminDashboard(Model model) {
        model.addAttribute("applications", loanApplicationRepository.findAll());
        return "admin";
    }

    @GetMapping("/offers")
    public String offers() {
        return "offers";
    }

    @GetMapping("/emi")
    public String emi() {
        return "emi";
    }

    @GetMapping("/support")
    public String support() {
        return "support";
    }
    
    @GetMapping("/history")
    public String applicationHistory(Model model) {

        model.addAttribute("applications", loanApplicationRepository.findAll());

        return "history";
    }

    @PostMapping("/apply")
    public String applyLoan(@ModelAttribute LoanApplication loanApplication, Model model) {

        LoanApplication saved = loanService.processLoan(loanApplication);

        BankOffer offer = bankOfferService.getOffer(saved.getRiskScore());

        model.addAttribute("result", saved);
        model.addAttribute("offer", offer);

        return "result";
    }
}