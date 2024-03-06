package com.banck.account.service.impl;

import com.banck.account.constant.AccountConstant;
import com.banck.account.dto.AccountDto;
import com.banck.account.dto.CustomerDto;
import com.banck.account.entity.Accounts;
import com.banck.account.entity.Customer;
import com.banck.account.exception.CustomerAlreadyExistsException;
import com.banck.account.exception.ResourceNotFoundException;
import com.banck.account.mapper.AccountMapper;
import com.banck.account.mapper.CustomerMapper;
import com.banck.account.repo.AccountsRepository;
import com.banck.account.repo.CustomerRepository;
import com.banck.account.service.IAccountsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;

@Service
public class AccountsServiceImpl implements IAccountsService {
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private AccountsRepository accountsRepository;

    /**
     * @param customerDto - CustomerDto Object store cutsomer and create a account
     */
    @Override
    public void createAccount(CustomerDto customerDto) {

        Customer customer = CustomerMapper.mapToCustomer(customerDto, new Customer());

        Optional<Customer> presentCustomer = this.customerRepository.findByMobileNumber(customer.getMobileNumber());
        if (presentCustomer.isPresent()) {
            throw new CustomerAlreadyExistsException("Customer already register with given mobileNumber " + customer.getMobileNumber());
        }
        customer.setCreatedAt(LocalDateTime.now());
        customer.setCreatedBy(customer.getName());
        Customer savedCustomer = this.customerRepository.save(customer);
        Accounts newAccount = createNewAccount(savedCustomer);
        this.accountsRepository.save(newAccount);

    }

    /**
     * @param customer create a new Account
     * @return
     */
    private Accounts createNewAccount(Customer customer) {
        Accounts accounts = new Accounts();
        accounts.setCustomerId(customer.getCustomerId());
        long randomNumber = 100000000000L * new Random().nextInt(900000000);
        accounts.setAccountNumber(randomNumber);
        accounts.setAccountType(AccountConstant.SAVINGS);
        accounts.setBranchAddress(AccountConstant.ADDRESS);
        accounts.setCreatedAt(LocalDateTime.now());
        accounts.setCreatedBy("hello");

        return accounts;
    }

    /**
     * @param mobileNumber - Input Mobile Number
     * @return
     */

    @Override
    public CustomerDto fetchAccount(String mobileNumber) {
        Customer customer = this.customerRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Customer", "Mobile No", mobileNumber)
        );
        Accounts accounts = this.accountsRepository.findByCustomerId(customer.getCustomerId()).orElseThrow(
                () -> new ResourceNotFoundException("Account", "Customer Id", String.valueOf(customer.getCustomerId()))
        );
        CustomerDto customerDto = CustomerMapper.mapToCustomerDto(customer, new CustomerDto());
        AccountDto accountDto = AccountMapper.mapToAccountsDto(accounts, new AccountDto());
        customerDto.setAccountsDto(accountDto);
        return customerDto;
    }

    /**
     * @param customerDto - CustomerDto Object
     * @return
     */

    @Override
    public boolean updateAccount(CustomerDto customerDto) {
        return false;
    }

    /**
     * @param mobileNumber - Input Mobile Number
     * @return
     */

    @Override
    public boolean deleteAccount(String mobileNumber) {
        return false;
    }
}
