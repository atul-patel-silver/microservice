package com.banck.account.service.impl;

import com.banck.account.constant.AccountConstant;
import com.banck.account.dto.AccountDto;
import com.banck.account.dto.AccountsMsgDto;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;


import java.util.Optional;
import java.util.Random;


@Service
public class AccountsServiceImpl implements IAccountsService {
    private static final Logger log = LoggerFactory.getLogger(AccountsServiceImpl.class);
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private AccountsRepository accountsRepository;
    @Autowired
    private  StreamBridge streamBridge;

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
        Customer savedCustomer = this.customerRepository.save(customer);
        Accounts newAccount = createNewAccount(savedCustomer);
        Accounts savedAccount = this.accountsRepository.save(newAccount);
        sendCommunication(savedAccount, savedCustomer);
    }


    private void sendCommunication(Accounts account, Customer customer) {
        var accountsMsgDto = new AccountsMsgDto(account.getAccountNumber(), customer.getName(),
                customer.getEmail(), customer.getMobileNumber());
        log.info("Sending Communication request for the details: {}", accountsMsgDto);
        var result = streamBridge.send("sendCommunication-out-0", accountsMsgDto);
        log.info("Is the Communication request successfully triggered ? : {}", result);
    }


    /**
     * @param customer create a new Account
     * @return
     */
    private Accounts createNewAccount(Customer customer) {
        Accounts accounts = new Accounts();
        accounts.setCustomerId(customer.getCustomerId());
        long randomNumber = 100000000000L + new Random().nextInt(900000000);
        accounts.setAccountNumber(randomNumber);
        accounts.setAccountType(AccountConstant.SAVINGS);
        accounts.setBranchAddress(AccountConstant.ADDRESS);


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
     * @param customerDto - CustomerDto Object  update account
     * @return
     */

    @Override
    public boolean updateAccount(CustomerDto customerDto) {
        boolean isUpdated = false;
        AccountDto accountsDto = customerDto.getAccountsDto();
        if(accountsDto !=null ){
            Accounts accounts = accountsRepository.findById(accountsDto.getAccountNumber()).orElseThrow(
                    () -> new ResourceNotFoundException("Account", "AccountNumber", accountsDto.getAccountNumber().toString())
            );
            AccountMapper.mapToAccounts(accountsDto, accounts);
            accounts = accountsRepository.save(accounts);

            Long customerId = accounts.getCustomerId();
            Customer customer = customerRepository.findById(customerId).orElseThrow(
                    () -> new ResourceNotFoundException("Customer", "CustomerID", customerId.toString())
            );
            CustomerMapper.mapToCustomer(customerDto,customer);
            customerRepository.save(customer);
            isUpdated = true;
        }
        return  isUpdated;
    }

    /**
     * @param mobileNumber - Input Mobile Number
     * @return boolean indicating if the delete of Account details is successful or not
     */

    @Override
    public boolean deleteAccount(String mobileNumber) {
        Customer customer = customerRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Customer", "mobileNumber", mobileNumber)
        );
        accountsRepository.deleteByCustomerId(customer.getCustomerId());
        customerRepository.deleteById(customer.getCustomerId());
        return true;
    }
}
