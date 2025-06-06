import { Component, computed, effect, inject, signal } from '@angular/core';
import { CurrencyPipe } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { Item, ItemService } from '../services/item.service';
import { CartService } from '../services/cart.service';
import { OfferService } from '../services/offer.service';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatToolbar } from '@angular/material/toolbar';
import { MatGridListModule } from '@angular/material/grid-list';
import { RouterModule } from '@angular/router';
import { formatOffer } from '../shared/utils';

interface ItemWithQuantity extends Item {
  quantity: number;
  specialOffer?: string;
}

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [
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

  private readonly itemService = inject(ItemService);
  private readonly offerService = inject(OfferService);
  readonly cartService = inject(CartService);

  readonly items = signal<ItemWithQuantity[]>([]);
  readonly loading = signal(false);
  readonly error = signal<string | null>(null);
  readonly total = computed(() => this.cartService.total());

  constructor() {
    this.loadData();
    this.setupProductList();
  }

  private setupProductList(): void {
    effect(() => {
      const [items, offers, cart] = [
        this.itemService.items(),
        this.offerService.offers(),
        this.cartService.items()
      ];

      this.items.set(items.map(item => ({
        ...item,
        quantity: cart.find(ci => ci.item.id === item.id)?.quantity ?? 0,
        specialOffer: formatOffer(item, offers)
      })));
    });
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
      this.error.set('Error to load data.');
    } finally {
      this.loading.set(false);
    }
  }

  increaseQuantity(item: ItemWithQuantity): void {
    this.cartService.scanItem(item.id);
  }

  decreaseQuantity(item: ItemWithQuantity): void {
    this.cartService.decreaseItem(item.id);
  }

}



