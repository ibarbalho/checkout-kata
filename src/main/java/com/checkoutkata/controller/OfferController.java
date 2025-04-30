package com.checkoutkata.controller;

import com.checkoutkata.domain.Offer;
import com.checkoutkata.service.OfferService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/offers")
public class OfferController {

    private final OfferService offerService;

    public OfferController(OfferService offerService) {
        this.offerService = offerService;
    }

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

    @GetMapping
    public ResponseEntity<List<Offer>> getAllOffers() {
        return ResponseEntity.ok(offerService.getAllOffers());
    }

    @DeleteMapping("/{offerId}")
    public ResponseEntity<Void> deleteOffer(@PathVariable Long offerId) {
        boolean deleted = offerService.deleteOffer(offerId);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
