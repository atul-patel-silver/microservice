package com.banck.account.service.client;

import com.banck.account.dto.CardDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "Cards",fallback = CardFallback.class)
public interface CardFeignClient {

    @GetMapping(value = "/card/fetch",consumes = "application/json")
    public ResponseEntity<CardDto> fetchCardDetails(@RequestHeader("sbi-bank-correlation-id")
                                                        String correlationId
            ,@RequestParam String mobileNumber);
}
