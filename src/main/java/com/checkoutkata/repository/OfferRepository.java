package com.checkoutkata.repository;

import com.checkoutkata.domain.Item;
import com.checkoutkata.domain.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OfferRepository extends JpaRepository<Offer, Long> {
    Optional<Offer> findByItem(Item item);
}
