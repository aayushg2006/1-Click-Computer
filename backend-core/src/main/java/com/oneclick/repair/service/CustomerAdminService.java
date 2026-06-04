package com.oneclick.repair.service;

import com.oneclick.repair.dto.admin.CustomerAdminDTO;
import com.oneclick.repair.model.Customer;
import com.oneclick.repair.repository.CustomerRepository;
import com.oneclick.repair.repository.LedgerAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerAdminService {

    private final CustomerRepository customerRepository;
    private final LedgerAccountRepository ledgerAccountRepository;

    @Transactional(readOnly = true)
    public List<CustomerAdminDTO> listCustomers() {
        return customerRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CustomerAdminDTO> searchCustomers(String query) {
        String q = query == null ? "" : query.trim();
        return customerRepository.findByFullNameContainingIgnoreCaseOrPhoneNumberContainingIgnoreCaseOrderByCreatedAtDesc(q, q)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<CustomerAdminDTO> findByPhone(String phone) {
        return customerRepository.findByPhoneNumber(phone).map(this::toDto);
    }

    private CustomerAdminDTO toDto(Customer customer) {
        return CustomerAdminDTO.builder()
                .id(customer.getId())
                .fullName(customer.getFullName())
                .phoneNumber(customer.getPhoneNumber())
                .address(customer.getAddress())
                .currentBalance(ledgerAccountRepository.findByCustomerId(customer.getId())
                        .map(account -> account.getCurrentBalance())
                        .orElse(null))
                .createdAt(customer.getCreatedAt())
                .build();
    }
}
