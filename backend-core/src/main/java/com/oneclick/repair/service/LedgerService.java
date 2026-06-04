package com.oneclick.repair.service;

import com.oneclick.repair.dto.AddLedgerTransactionRequest;
import com.oneclick.repair.dto.LedgerSummaryDTO;
import com.oneclick.repair.model.Customer;
import com.oneclick.repair.model.LedgerAccount;
import com.oneclick.repair.model.LedgerTransaction;
import com.oneclick.repair.repository.CustomerRepository;
import com.oneclick.repair.repository.LedgerAccountRepository;
import com.oneclick.repair.repository.LedgerTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LedgerService {

    private final LedgerAccountRepository ledgerAccountRepository;
    private final LedgerTransactionRepository ledgerTransactionRepository;
    private final CustomerRepository customerRepository;

    @Transactional
    public String addTransaction(AddLedgerTransactionRequest request) {
        
        // 1. Find the customer
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found!"));

        // 2. Find their Khata account. If they don't have one yet, create a brand new one!
        // (Assuming you have added a findByCustomerId method to your LedgerAccountRepository)
        LedgerAccount account = ledgerAccountRepository.findByCustomerId(customer.getId())
                .orElseGet(() -> {
                    LedgerAccount newAccount = LedgerAccount.builder()
                            .customer(customer)
                            .currentBalance(BigDecimal.ZERO)
                            .build();
                    return ledgerAccountRepository.save(newAccount);
                });

        // 3. Do the accounting math safely
        if (request.getTransactionType().equalsIgnoreCase("DEBIT")) {
            // DEBIT means they owe you more money (Balance goes UP)
            account.setCurrentBalance(account.getCurrentBalance().add(request.getAmount()));
        } else if (request.getTransactionType().equalsIgnoreCase("CREDIT")) {
            // CREDIT means they paid you (Balance goes DOWN)
            account.setCurrentBalance(account.getCurrentBalance().subtract(request.getAmount()));
        } else {
            throw new RuntimeException("Invalid Transaction Type. Must be DEBIT or CREDIT.");
        }

        ledgerAccountRepository.save(account);

        // 4. Save the permanent receipt of this action exactly matching your LedgerTransaction model
        LedgerTransaction transaction = LedgerTransaction.builder()
                .ledgerAccount(account)
                .transactionType(request.getTransactionType().toUpperCase())
                .amount(request.getAmount())
                .remarks(request.getRemarks())
                .build();

        ledgerTransactionRepository.save(transaction);

        return "Transaction saved successfully! New Balance: ₹" + account.getCurrentBalance();
    }

    @Transactional(readOnly = true)
    public LedgerSummaryDTO getCustomerLedger(UUID customerId) {
        
        LedgerAccount account = ledgerAccountRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new RuntimeException("No Khata found for this customer."));

        // Package the transactions into the safe DTO list
        List<LedgerSummaryDTO.TransactionRecord> history = account.getTransactions().stream()
                .map(t -> LedgerSummaryDTO.TransactionRecord.builder()
                        .transactionType(t.getTransactionType())
                        .amount(t.getAmount())
                        .remarks(t.getRemarks())
                        .date(t.getTransactionDate())
                        .build())
                .collect(Collectors.toList());

        return LedgerSummaryDTO.builder()
                .ledgerAccountId(account.getId())
                .customerName(account.getCustomer() != null ? account.getCustomer().getFullName() : null)
                .customerPhone(account.getCustomer() != null ? account.getCustomer().getPhoneNumber() : null)
                .currentBalance(account.getCurrentBalance())
                .recentTransactions(history)
                .build();
    }

    @Transactional(readOnly = true)
    public List<LedgerSummaryDTO> getOverdueLedgers(BigDecimal threshold) {
        return ledgerAccountRepository.findByCurrentBalanceLessThan(threshold).stream()
                .map(account -> LedgerSummaryDTO.builder()
                        .ledgerAccountId(account.getId())
                        .customerName(account.getCustomer() != null ? account.getCustomer().getFullName() : null)
                        .customerPhone(account.getCustomer() != null ? account.getCustomer().getPhoneNumber() : null)
                        .currentBalance(account.getCurrentBalance())
                        .recentTransactions(account.getTransactions().stream()
                                .map(t -> LedgerSummaryDTO.TransactionRecord.builder()
                                        .transactionType(t.getTransactionType())
                                        .amount(t.getAmount())
                                        .remarks(t.getRemarks())
                                        .date(t.getTransactionDate())
                                        .build())
                                .collect(Collectors.toList()))
                        .build())
                .toList();
    }
}
