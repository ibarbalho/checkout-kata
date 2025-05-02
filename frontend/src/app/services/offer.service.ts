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

  constructor(private http: HttpClient) { }

  // GET /offers
  getAllOffers(): void {
    this.http.get<Offer[]>(this.apiUrl).subscribe({
      next: (offers) => {
        this.offers.set(offers);
      },
      error: (error) => {
        console.error('Error fetching offers:', error);
      }
    });
  }

  // POST /offers/{itemId}
  createOffer(itemId: number, offer: { quantity: number, totalPrice: number }): void {
    this.http.post<Offer>(`${this.apiUrl}/${itemId}`, offer).subscribe({
      next: (newOffer) => {
        this.offers.update(offers => [...offers, newOffer]);
      },
      error: (error) => {
        console.error('Error creating offer:', error);
      }
    });
  }

  // PUT /offers/{id}
  updateOffer(id: number, offer: Partial<Offer>): void {
    this.http.put<Offer>(`${this.apiUrl}/${id}`, offer).subscribe({
      next: (updatedOffer) => {
        this.offers.update(offers =>
          offers.map(o => o.id === updatedOffer.id ? updatedOffer : o)
        );
      },
      error: (error) => {
        console.error('Error updating offer:', error);
      }
    });
  }

  // DELETE /offers/{offerId}
  deleteOffer(offerId: number): void {
    this.http.delete<void>(`${this.apiUrl}/${offerId}`).subscribe({
      next: () => {
        this.offers.update(offers => offers.filter(o => o.id !== offerId));
      },
      error: (error) => {
        console.error('Error deleting offer:', error);
      }
    });
  }
}
