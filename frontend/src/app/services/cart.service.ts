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
  readonly error = signal<string | null>(null);

  constructor(private http: HttpClient) {
    this.loadCart();
  }

  private loadCart(): void {
    this.getCartContents();
    this.getCartTotal();
  }

  scanItem(id: number): void {
    this.http.post<CartItem>(`${this.apiUrl}/scan/${id}`, {})
      .subscribe({
        next: () => {
          this.getCartContents();
          this.getCartTotal();
        },
        error: () => this.error.set('Failed to scan item')
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
    this.http.put<CartItem | null>(`${this.apiUrl}/items/${id}/decrease`, null, {
      params: { quantity: quantity.toString() }
    }).subscribe({
      next: () => {
        this.getCartContents();
        this.getCartTotal();
      },
      error: () => this.error.set('Failed to decrease item quantity')
    });
  }

  removeItem(id: number): void {
    this.http.delete(`${this.apiUrl}/items/${id}`)
      .subscribe({
        next: () => {
          this.getCartContents();
          this.getCartTotal();
        },
        error: () => this.error.set('Failed to remove item')
      });
  }

  clearCart(): void {
    this.http.delete(this.apiUrl)
      .subscribe({
        next: () => {
          this.items.set([]);
          this.total.set(0);
        },
        error: () => this.error.set('Failed to clear cart')
      });
  }
}
