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

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * Service class managing special price offers for items in the shopping system.
 * It allows creating, updating, retrieving, and deleting offers.
 */
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

    /**
     * Creates a new offer for a specific item.
     * @param itemId ID of the item
     * @param offerData Offer data to create
     * @return Created offer
     * @throws EntityNotFoundException if item not found
     * @throws IllegalArgumentException if offer data is invalid
     */
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

    /**
     * Updates an existing offer.
     * @param offerId ID of the offer to update
     * @param offerData New offer data
     * @return Updated offer
     * @throws EntityNotFoundException if offer not found
     * @throws IllegalArgumentException if offer data is invalid
     */
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

    /**
     * Retrieves all offers.
     * @return List of all offers
     */
    public List<Offer> getAllOffers() {
        List<Offer> offers = offerRepository.findAll();
        logger.info("Found {} offers", offers.size());
        return offers;
    }

    /**
     * Retrieves an offer by its ID.
     * @param id ID of the offer
     * @return The offer with the specified ID
     * @throws EntityNotFoundException if offer not found
     */
    public Offer getOfferById(Long id) {
        logger.debug("Fetching offer with id: {}", id);
        return offerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Offer not found: " + id));
    }

    /**
     * Deletes an offer by its ID.
     * @param offerId ID of the offer to delete
     * @return true if deleted successfully, false if not found
     */
    public boolean deleteOffer(Long offerId) {
        if (!offerRepository.existsById(offerId)) {
            logger.warn("Offer not found with id: {}", offerId);
            return false;
        }
        offerRepository.deleteById(offerId);
        logger.info("Successfully deleted offer with id: {}", offerId);
        return true;
    }

    /**
     * Validates the offer data.
     * @param offer The offer to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateOfferData(Offer offer) {
        if (offer.getQuantity() <= 0) {
            throw new IllegalArgumentException("Offer quantity must be positive");
        }
        if (offer.getTotalPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Offer price cannot be negative");
        }
    }
}
