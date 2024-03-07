package com.bank.loan.services.imple;

import com.bank.loan.constants.CardsConstants;
import com.bank.loan.dto.CardDto;
import com.bank.loan.entity.Card;
import com.bank.loan.exception.CardAlreadyExistsException;
import com.bank.loan.exception.ResourceNotFoundException;
import com.bank.loan.mapper.CardsMapper;
import com.bank.loan.repository.CardsRepository;
import com.bank.loan.services.ICardsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
public class CardServiceImple implements ICardsService {

    @Autowired
    private CardsRepository cardsRepository;

    /**
     *
     * @param mobileNumber - Mobile Number of the Customer
     */
    @Override
    public void createCard(String mobileNumber) {
        Optional<Card> oldCard = this.cardsRepository.findCardByMobileNumber(mobileNumber);
        if(oldCard.isPresent()){
            throw  new CardAlreadyExistsException("Card already registered with given mobileNumber "+mobileNumber);
        }

        this.cardsRepository.save(createNewCard(mobileNumber));
    }

    /**
     *
     * @param mobileNumber
     * @return
     */
    private Card createNewCard(String mobileNumber) {

        Card newCard=new Card();
        long randomCardNumber = 100000000000L + new Random().nextInt(900000000);
        newCard.setCardNumber(Long.toString(randomCardNumber));
        newCard.setMobileNumber(mobileNumber);
        newCard.setCardType(CardsConstants.CREDIT_CARD);
        newCard.setTotalLimit(CardsConstants.NEW_CARD_LIMIT);
        newCard.setAmountUsed(0);
        newCard.setAvailableAmount(CardsConstants.NEW_CARD_LIMIT);

        return newCard;
    }

    /**
     *
     * @param mobileNumber - Input mobile Number
     * @return
     */
    @Override
    public CardDto fetchCard(String mobileNumber) {
        Card card = this.cardsRepository.findCardByMobileNumber(mobileNumber).orElseThrow(() -> new ResourceNotFoundException("Card", "mobileNumber", mobileNumber));
        return CardsMapper.mapTocardDto(card,new CardDto());
    }

    /**
     *
     * @param cardsDto - CardDto Object
     * @return
     */
    @Override
    public boolean updateCard(CardDto cardsDto) {
        Card card = this.cardsRepository.findCardByCardNumber(cardsDto.getCardNumber()).orElseThrow(() -> new ResourceNotFoundException("Card", "CardNumber", cardsDto.getCardNumber()));
         CardsMapper.mapTocard(cardsDto,card);
         this.cardsRepository.save(card);
        return true;
    }

    /**
     *
     * @param mobileNumber - Input Mobile Number
     * @return
     */

    @Override
    public boolean deleteCard(String mobileNumber) {
        Card card = this.cardsRepository.findCardByMobileNumber(mobileNumber).orElseThrow(() -> new ResourceNotFoundException("Card", "mobileNumber", mobileNumber));
        this.cardsRepository.deleteById(card.getCardId());
        return true;
    }
}
