import { HttpClient } from '@angular/common/http';
import { Injectable, signal } from '@angular/core';
import { environment } from '../../environments/environment';

export interface CartItem {
  id: number;
  item: {
    id: number;
    name: string;
    unitPrice: number;
  };
  quantity: number;
}


@Injectable({
  providedIn: 'root'
})
export class CartService {
  private readonly apiUrl = `${environment.apiUrl}/cart`;

  readonly items = signal<CartItem[]>([]);
  readonly total = signal(0);
  readonly loading = signal(false);
  readonly error = signal<string | null>(null);

  constructor(private http: HttpClient) {
    this.loadCart();
  }

  private loadCart(): void {
    this.loading.set(true);
    this.getCartContents();
    this.getCartTotal();
    this.loading.set(false);
  }

  scanItem(id: number): void {
    this.loading.set(true);
    this.http.post<CartItem>(`${this.apiUrl}/scan/${id}`, {})
      .subscribe({
        next: () => {
          this.getCartContents();
          this.getCartTotal();
        },
        error: () => this.error.set('Failed to scan item'),
        complete: () => this.loading.set(false)
      });
  }

  getCartContents(): void {
    this.http.get<CartItem[]>(`${this.apiUrl}/contents`)
      .subscribe({
        next: (items) => this.items.set(items),
        error: () => this.error.set('Failed to load cart contents')
      });
  }

  getCartTotal(): void {
    this.http.get<{ total: number }>(`${this.apiUrl}/total`)
      .subscribe({
        next: (response) => this.total.set(response.total),
        error: () => this.error.set('Failed to get cart total')
      });
  }

  decreaseItem(id: number, quantity: number = 1): void {
    this.loading.set(true);
    this.http.put<CartItem | null>(`${this.apiUrl}/items/${id}/decrease`, null, {
      params: { quantity: quantity.toString() }
    }).subscribe({
      next: () => {
        this.getCartContents();
        this.getCartTotal();
      },
      error: () => this.error.set('Failed to decrease item quantity'),
      complete: () => this.loading.set(false)
    });
  }

  removeItem(id: number): void {
    this.loading.set(true);
    this.http.delete(`${this.apiUrl}/items/${id}`)
      .subscribe({
        next: () => {
          this.getCartContents();
          this.getCartTotal();
        },
        error: () => this.error.set('Failed to remove item'),
        complete: () => this.loading.set(false)
      });
  }

  clearCart(): void {
    this.loading.set(true);
    this.http.delete(this.apiUrl)
      .subscribe({
        next: () => {
          this.items.set([]);
          this.total.set(0);
        },
        error: () => this.error.set('Failed to clear cart'),
        complete: () => this.loading.set(false)
      });
  }
}
