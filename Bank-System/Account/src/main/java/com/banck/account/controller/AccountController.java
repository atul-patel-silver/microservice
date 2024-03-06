package com.banck.account.controller;

import com.banck.account.constant.AccountConstant;
import com.banck.account.dto.CustomerDto;
import com.banck.account.dto.ResponseDto;
import com.banck.account.service.IAccountsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/account", produces = {MediaType.APPLICATION_JSON_VALUE})
public class AccountController {


    @Autowired
    private IAccountsService iAccountsService;

    /**
     * @param customerDto end point of create account of user
     * @return
     */

    @PostMapping("/create")
    public ResponseEntity<ResponseDto> createAccount(@RequestBody CustomerDto customerDto) {

        this.iAccountsService.createAccount(customerDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDto(AccountConstant.STATUS_201, AccountConstant.MESSAGE_201));
    }

    @GetMapping("/fetch")
    public ResponseEntity<CustomerDto> fetchAccountDetail(@RequestParam String mobileNUmber){

        return
    }


}