import { Component, computed, effect, signal } from '@angular/core';
import { NgFor, CurrencyPipe } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { Item, ItemService } from '../services/item.service';
import { CartService } from '../services/cart.service';
import { Offer, OfferService } from '../services/offer.service';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatToolbar } from '@angular/material/toolbar';
import { MatGridListModule } from '@angular/material/grid-list';
import { Router, RouterModule } from '@angular/router';

interface ItemWithQuantity extends Item {
  quantity: number;
  specialOffer?: string;
}

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [
    NgFor,
    CurrencyPipe,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatDividerModule,
    MatProgressSpinnerModule,
    MatToolbar,
    MatGridListModule, RouterModule
  ],
  templateUrl: './product-list.component.html',
  styleUrls: ['./product-list.component.scss']
})
export class ProductListComponent {

  readonly items = signal<ItemWithQuantity[]>([]);
  readonly loading = signal(false);
  readonly error = signal<string | null>(null);
  readonly total = computed(() => this.cartService.total());

  constructor(private itemService: ItemService,
    readonly cartService: CartService,
    private offerService: OfferService,
     private router: Router) {

    this.loadData();

    effect(() => {
      const items = this.itemService.items();
      const offers = this.offerService.offers();
      const cartItems = this.cartService.items();

      this.items.set(
        items.map(item => {
          const cartItem = cartItems.find(ci => ci.item.id === item.id);
          return {
            ...item,
            quantity: cartItem?.quantity ?? 0,
            specialOffer: this.getSpecialOffer(item, offers)
          };
        })
      );
    });
  }

  goToCheckout() {
    this.router.navigate(['/checkout']);
  }

  private async loadData(): Promise<void> {
    this.loading.set(true);
    try {
      await Promise.all([
        this.itemService.getAllItems(),
        this.offerService.getAllOffers(),
        this.cartService.getCartContents(),
        this.cartService.getCartTotal()
      ]);
    } catch (err) {
      this.error.set('Erro ao carregar os dados.');
    } finally {
      this.loading.set(false);
    }
  }

  private getSpecialOffer(item: Item, offers: Offer[]): string | undefined {
    const offer = offers.find(o => o.item.id === item.id);
    return offer ? `${offer.quantity} for €${(offer.totalPrice / 100).toFixed(2)}` : undefined;
  }

  increaseQuantity(item: ItemWithQuantity): void {
    this.cartService.scanItem(item.id);
  }

  decreaseQuantity(item: ItemWithQuantity): void {
    this.cartService.decreaseItem(item.id);
  }

}



