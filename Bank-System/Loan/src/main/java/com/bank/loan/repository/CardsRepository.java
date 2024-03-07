package com.bank.loan.repository;

import com.bank.loan.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface CardsRepository extends JpaRepository<Card,Long> {

    Optional<Card> findCardByMobileNumber(String mobileNumber);

    Optional<Card> findCardByCardNumber(String cardNumber);


}
