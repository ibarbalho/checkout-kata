import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { MatFormFieldModule } from '@angular/material/form-field';
import { ItemService } from '../services/item.service';
import { OfferService } from '../services/offer.service';
import { MatInputModule } from '@angular/material/input';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-add-offer-dialog',
  standalone: true,
  imports: [
    CommonModule,
    MatDialogModule,
    MatButtonModule,
    MatSelectModule,
    MatFormFieldModule,
    MatInputModule,
    FormsModule
  ],
  templateUrl: './add-offer-dialog.component.html',
  styleUrl: './add-offer-dialog.component.scss'
})
export class AddOfferDialogComponent  implements OnInit {
  selectedItemId: number | null = null; 
  quantity: number = 0;
  totalPrice: number = 0;
  itemService = inject(ItemService);
  offerService = inject(OfferService);
  dialogRef = inject(MatDialogRef<AddOfferDialogComponent>);

  ngOnInit(): void {
    this.loadItems();
  }

  loadItems(): void {
    this.itemService.getAllItems();
  }

  createOffer(): void {
    if (this.selectedItemId) {
      this.offerService.createOffer(this.selectedItemId, {
        quantity: this.quantity,
        totalPrice: this.totalPrice
      });
      this.dialogRef.close(true); 
    }
  }
}
