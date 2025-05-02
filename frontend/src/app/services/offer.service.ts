import { HttpClient } from '@angular/common/http';
import { Injectable, signal } from '@angular/core';
import { environment } from '../../environments/environment';

export interface Offer {
  id: number;
  quantity: number;
  totalPrice: number;
  item: {
    id: number;
    name: string;
    unitPrice: number;
  };
}

@Injectable({
  providedIn: 'root'
})
export class OfferService {
  private readonly apiUrl = `${environment.apiUrl}/offers`;

  public readonly offers = signal<Offer[]>([]);
  public readonly loading = signal<boolean>(false);

  constructor(private http: HttpClient) { }

  // GET /offers
  getAllOffers(): void {
    this.loading.set(true);
    this.http.get<Offer[]>(this.apiUrl).subscribe({
      next: (offers) => {
        this.offers.set(offers);
        this.loading.set(false);
      },
      error: (error) => {
        console.error('Error fetching offers:', error);
        this.loading.set(false);
      }
    });
  }

  // POST /offers/{itemId}
  createOffer(itemId: number, offer: { quantity: number, totalPrice: number }): void {
    this.loading.set(true);
    this.http.post<Offer>(`${this.apiUrl}/${itemId}`, offer).subscribe({
      next: (newOffer) => {
        this.offers.update(offers => [...offers, newOffer]);
        this.loading.set(false);
      },
      error: (error) => {
        console.error('Error creating offer:', error);
        this.loading.set(false);
      }
    });
  }

  // PUT /offers/{id}
  updateOffer(id: number, offer: Partial<Offer>): void {
    this.loading.set(true);
    this.http.put<Offer>(`${this.apiUrl}/${id}`, offer).subscribe({
      next: (updatedOffer) => {
        this.offers.update(offers =>
          offers.map(o => o.id === updatedOffer.id ? updatedOffer : o)
        );
        this.loading.set(false);
      },
      error: (error) => {
        console.error('Error updating offer:', error);
        this.loading.set(false);
      }
    });
  }

  // DELETE /offers/{offerId}
  deleteOffer(offerId: number): void {
    this.loading.set(true);
    this.http.delete<void>(`${this.apiUrl}/${offerId}`).subscribe({
      next: () => {
        this.offers.update(offers => offers.filter(o => o.id !== offerId));
        this.loading.set(false);
      },
      error: (error) => {
        console.error('Error deleting offer:', error);
        this.loading.set(false);
      }
    });
  }
}
