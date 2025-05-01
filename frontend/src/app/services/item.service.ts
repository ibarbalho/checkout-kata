import { HttpClient } from '@angular/common/http';
import { computed, Injectable, signal } from '@angular/core';
import { environment } from '../../environments/environment';
import { Observable } from 'rxjs';

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

  private readonly _items = signal<Item[]>([]);
  public readonly items = computed(() => this._items());

  private readonly _selectedItem = signal<Item | null>(null);
  public readonly selectedItem = computed(() => this._selectedItem());

  private readonly _createdItem = signal<Item | null>(null);
  public readonly createdItem = computed(() => this._createdItem());

  private readonly _updatedItem = signal<Item | null>(null);
  public readonly updatedItem = computed(() => this._updatedItem());

  constructor(private http: HttpClient) { }

   // GET /items
  getAllItems() {
    this.http.get<Item[]>(this.apiUrl).subscribe({
      next: (items) => this._items.set(items),
      error: (error) => console.error('Error fetching items:', error)
    });
  }

  // GET /items/:id
  getItemById(id: number) {
    this.http.get<Item>(`${this.apiUrl}/${id}`).subscribe({
      next: (item) => this._selectedItem.set(item),
      error: (error) => console.error('Error fetching item:', error)
    });
  }

  // PUT /items/:id
  createItem(item: Item): void {
    this.http.post<Item>(this.apiUrl, item).subscribe({
      next: (createdItem) => {
        this._createdItem.set(createdItem);
        // Update items list with new item
        this._items.update(items => [...items, createdItem]);
      },
      error: (error) => {
        console.error('Error creating item:', error);
        this._createdItem.set(null);
      }
    });
  }

  updateItemPrice(id: number, price: number): void {
    const updateData = { id, unitPrice: price } as Item;
    
    this.http.put<Item>(`${this.apiUrl}/${id}`, updateData)
      .subscribe({
        next: (updatedItem) => {
          this._updatedItem.set(updatedItem);
          
          this._items.update(items => 
            items.map(item => 
              item.id === updatedItem.id ? updatedItem : item
            )
          );
        },
        error: (error) => {
          console.error('Error updating item:', error);
          this._updatedItem.set(null);
        }
      });
  }
}
