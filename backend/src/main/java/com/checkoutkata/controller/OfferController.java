package com.checkoutkata.controller;

import com.checkoutkata.domain.Offer;
import com.checkoutkata.service.OfferService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for managing special price offers in the shopping system.
 * Provides endpoints for creating, updating, retrieving, and deleting offers
 * that define special pricing rules for items when bought in specific quantities.
 */
@RestController
@RequestMapping("/offers")
public class OfferController {

    private final OfferService offerService;

    public OfferController(OfferService offerService) {
        this.offerService = offerService;
    }

    /**
     * Creates a new offer for a specific item.
     *
     * @param itemId the ID of the item to which the offer applies
     * @param offer   the offer details
     * @return the created offer
     */
    @PostMapping("/{itemId}")
    public ResponseEntity<Offer> createOffer(@PathVariable Long itemId, @RequestBody Offer offer) {
        try {
            return ResponseEntity.ok(offerService.createOffer(itemId, offer));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Updates an existing offer.
     *
     * @param id    the ID of the offer to update
     * @param offer the updated offer details
     * @return the updated offer
     */
    @PutMapping("/{id}")
    public ResponseEntity<Offer> updateOffer(@PathVariable Long id, @RequestBody Offer offer) {
        try {
            return ResponseEntity.ok(offerService.updateOffer(id, offer));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Retrieves all active offers in the system.
     * @return ResponseEntity containing list of all offers
     */
    @GetMapping
    public ResponseEntity<List<Offer>> getAllOffers() {
        return ResponseEntity.ok(offerService.getAllOffers());
    }

    /**
     * Deletes an existing offer.
     * @param offerId ID of the offer to delete
     * @return ResponseEntity with 204 if deleted, or 404 if not found
     */
    @DeleteMapping("/{offerId}")
    public ResponseEntity<Void> deleteOffer(@PathVariable Long offerId) {
        boolean deleted = offerService.deleteOffer(offerId);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
