package com.banck.account.service.client;


import com.banck.account.dto.LoansDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "Loan",fallback = LoansFallback.class)
public interface LoansFeignClient {

    @GetMapping(value = "/loan/fetch",consumes = "application/json")
    public ResponseEntity<LoansDto> fetchLoanDetails(@RequestHeader("sbi-bank-correlation-id")
                                                         String correlationId,@RequestParam String mobileNumber);

}
