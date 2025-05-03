import { Component, effect, inject } from '@angular/core';
import { Item, ItemService } from '../services/item.service';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { EditItemDialogComponent } from '../edit-item-dialog/edit-item-dialog.component';
import { MatDialogModule } from '@angular/material/dialog';
import { AddItemDialogComponent } from '../add-item-dialog/add-item-dialog.component';

@Component({
  selector: 'app-items',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatToolbarModule,
    MatIconModule,
    MatButtonModule,
    MatDialogModule
  ],
  templateUrl: './items.component.html',
  styleUrl: './items.component.scss'
})
export class ItemsComponent {

  private readonly dialog = inject(MatDialog);
  readonly itemService = inject(ItemService);
 
  openAddItemDialog(): void {
    const dialogRef = this.dialog.open(AddItemDialogComponent, {
      width: '400px'
    });
  
    dialogRef.afterClosed().subscribe({
      next: (result) => {
        if (result) {
          this.itemService.createItem({
            id: Date.now(),
            name: result.name,
            unitPrice: result.unitPrice
          });
        }
      },
      error: (error) => {
        console.error('Error adding item:', error);
      }
    });
  }

  editItem(item: Item): void {
    const dialogRef = this.dialog.open(EditItemDialogComponent, {
      width: '400px',
      data: { ...item }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        if (result.name !== item.name) {
          this.itemService.updateItemName(item.id, result.name);
        }
        if (result.unitPrice !== item.unitPrice) {
          this.itemService.updateItemPrice(item.id, result.unitPrice);
        }
      }
    });
  }

  // TODO: You need to delete the associated offers first before deleting the item.
  deleteItem(id: number): void {   
    if (confirm('Are you sure you want to delete this item?')) {
      this.itemService.deleteItem(id);
    }
  }
}

