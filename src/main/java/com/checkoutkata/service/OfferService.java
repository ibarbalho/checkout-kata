package com.checkoutkata.service;

import com.checkoutkata.domain.Item;
import com.checkoutkata.domain.Offer;
import com.checkoutkata.repository.ItemRepository;
import com.checkoutkata.repository.OfferRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class OfferService {
    private static final Logger logger = LoggerFactory.getLogger(OfferService.class);

    private final OfferRepository offerRepository;
    private final ItemRepository itemRepository;

    public OfferService(OfferRepository offerRepository, ItemRepository itemRepository) {
        this.offerRepository = offerRepository;
        this.itemRepository = itemRepository;
    }

    public Offer createOffer(Long itemId, Offer offerData) {
        Objects.requireNonNull(offerData, "Offer data cannot be null");
        validateOfferData(offerData);

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Item not found: " + itemId));

        Offer offer = new Offer(item, offerData.getQuantity(), offerData.getTotalPrice());
        Offer savedOffer = offerRepository.save(offer);
        logger.info("Created new offer for item: {}, quantity: {}, price: {}",
                item.getName(), offer.getQuantity(), offer.getTotalPrice());
        return savedOffer;
    }

    public Offer updateOffer(Long offerId, Offer offerData) {
        validateOfferData(offerData);

        Offer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new EntityNotFoundException("Offer not found: " + offerId));

        offer.setQuantity(offerData.getQuantity());
        offer.setTotalPrice(offerData.getTotalPrice());
        Offer updatedOffer = offerRepository.save(offer);
        logger.info("Updated offer with id: {}, new quantity: {}, new price: {}",
                offerId, offerData.getQuantity(), offerData.getTotalPrice());
        return updatedOffer;
    }

    public List<Offer> getAllOffers() {
        List<Offer> offers = offerRepository.findAll();
        logger.info("Found {} offers", offers.size());
        return offers;
    }

    public Offer getOfferById(Long id) {
        logger.debug("Fetching offer with id: {}", id);
        return offerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Offer not found: " + id));
    }

    public boolean deleteOffer(Long offerId) {
        if (!offerRepository.existsById(offerId)) {
            logger.warn("Offer not found with id: {}", offerId);
            return false;
        }
        offerRepository.deleteById(offerId);
        logger.info("Successfully deleted offer with id: {}", offerId);
        return true;
    }

    private void validateOfferData(Offer offer) {
        if (offer.getQuantity() <= 0) {
            throw new IllegalArgumentException("Offer quantity must be positive");
        }
        if (offer.getTotalPrice() < 0) {
            throw new IllegalArgumentException("Offer price cannot be negative");
        }
    }
}
