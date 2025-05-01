import { HttpClient } from '@angular/common/http';
import { Injectable, signal } from '@angular/core';
import { environment } from '../../environments/environment';

export interface Item {
  id: number;
  name: string;
  unitPrice: number;
}

@Injectable({
  providedIn: 'root'
})
export class ItemService {

  private readonly apiUrl = `${environment.apiUrl}/items`;

  readonly items = signal<Item[]>([]);
  readonly selectedItem = signal<Item | null>(null);
  readonly createdItem = signal<Item | null>(null);
  readonly updatedItem = signal<Item | null>(null);

  constructor(private http: HttpClient) { }

  // GET /items
  getAllItems() {
    this.http.get<Item[]>(this.apiUrl).subscribe({
      next: (items) => this.items.set(items),
      error: (error) => console.error('Error fetching items:', error)
    });
  }

  // GET /items/:id
  getItemById(id: number) {
    this.http.get<Item>(`${this.apiUrl}/${id}`).subscribe({
      next: (item) => this.selectedItem.set(item),
      error: (error) => console.error('Error fetching item:', error)
    });
  }

  // POST /items
  createItem(item: Item): void {
    this.http.post<Item>(this.apiUrl, item).subscribe({
      next: (createdItem) => {
        this.createdItem.set(createdItem);

        this.items.update(items => [...items, createdItem]);
      },
      error: (error) => {
        console.error('Error creating item:', error);
        this.createdItem.set(null);
      }
    });
  }

  // PUT /items/:id
  updateItemPrice(id: number, price: number): void {
    const updateData = { id, unitPrice: price } as Item;

    this.http.put<Item>(`${this.apiUrl}/${id}`, updateData)
      .subscribe({
        next: (updatedItem) => {
          this.updatedItem.set(updatedItem);

          this.items.update(items =>
            items.map(item =>
              item.id === updatedItem.id ? updatedItem : item
            )
          );
        },
        error: (error) => {
          console.error('Error updating item:', error);
          this.updatedItem.set(null);
        }
      });
  }

  // PUT /items/:id/name
  updateItemName(id: number, name: string): void {
    const updateData = { id, name } as Item;

    this.http.put<Item>(`${this.apiUrl}/${id}/name`, updateData)
      .subscribe({
        next: (updatedItem) => {
          this.updatedItem.set(updatedItem);
          this.items.update(items =>
            items.map(item =>
              item.id === updatedItem.id ? updatedItem : item
            )
          );
        },
        error: (error) => {
          console.error('Error updating item name:', error);
          this.updatedItem.set(null);
        }
      });
  }

  // DELETE /items/:id
  deleteItem(id: number): void {
    this.http.delete<void>(`${this.apiUrl}/${id}`)
      .subscribe({
        next: () => {
          this.items.update(items =>
            items.filter(item => item.id !== id)
          );
        },
        error: (error) => {
          console.error('Error deleting item:', error);
        }
      });
  }
}
