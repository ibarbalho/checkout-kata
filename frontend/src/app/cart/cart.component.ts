import { Component, computed, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { CartItem, CartService } from '../services/cart.service';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { RouterModule } from '@angular/router';
import { formatOffer } from '../shared/utils';
import { OfferService } from '../services/offer.service';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatToolbarModule,
    MatProgressSpinnerModule,
    RouterModule
  ],
  templateUrl: './cart.component.html',
  styleUrl: './cart.component.scss'
})
export class CartComponent {
  readonly cartService = inject(CartService);
  private readonly offerService = inject(OfferService);

  increaseQuantity(item: CartItem): void {
    this.cartService.scanItem(item.item.id);
  }

  decreaseQuantity(item: CartItem): void {
    this.cartService.decreaseItem(item.item.id);
  }

  removeItem(item: CartItem): void {
    this.cartService.removeItem(item.item.id);
  }

  readonly getOffer = computed(() => (item: CartItem) => {
    const cartItem = {
      ...item.item,
      unitPrice: item.item.unitPrice.toString()
    };

    return formatOffer(cartItem, this.offerService.offers());
  });

  readonly calculateItemTotal = computed(() => (item: CartItem) => {
    const offer = this.offerService.offers().find(o => o.item.id === item.item.id);

    if (offer && item.quantity >= offer.quantity) {
      const offerGroups = Math.floor(item.quantity / offer.quantity);
      const remainingItems = item.quantity % offer.quantity;
      
      const offerTotal = this.multiply(offer.totalPrice.toString(), offerGroups);
      
      const remainingTotal = this.multiply(item.item.unitPrice.toString(), remainingItems);
      
      return this.add(offerTotal, remainingTotal);
    }

    return this.multiply(item.item.unitPrice.toString(), item.quantity);
  });

  private multiply(price: string, quantity: number): string {
    return (parseFloat(price) * quantity).toFixed(2);
  }

  private add(price1: string, price2: string): string {
    return (parseFloat(price1) + parseFloat(price2)).toFixed(2);
  }
}
