import { Component, effect, inject } from '@angular/core';
import { Offer, OfferService } from '../services/offer.service';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDialog } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule } from '@angular/material/dialog';
import { EditOfferDialogComponent } from '../edit-offer-dialog/edit-offer-dialog.component';
import { AddOfferDialogComponent } from '../add-offer-dialog/add-offer-dialog.component';

interface OfferData {
  itemId: number;
  quantity: number;
  totalPrice: number;
}

@Component({
  selector: 'app-offers',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatToolbarModule,
    MatProgressSpinnerModule,
    MatIconModule,
    MatButtonModule,
    MatDialogModule
  ],
  templateUrl: './offers.component.html',
  styleUrl: './offers.component.scss'
})
export class OffersComponent {

  private readonly dialog = inject(MatDialog);
  readonly offerService = inject(OfferService);

  openAddOfferDialog(): void {
    const dialogRef = this.dialog.open(AddOfferDialogComponent, {
      width: '400px'
    });
  
    dialogRef.afterClosed().subscribe({
      next: (offerData: OfferData) => {
        if (offerData) {
          this.offerService.createOffer(offerData.itemId, {
            quantity: offerData.quantity,
            totalPrice: offerData.totalPrice
          });
        }
      }
    });
  }

  editOffer(offer: Offer): void {
    const dialogRef = this.dialog.open(EditOfferDialogComponent, {
      width: '400px',
      data: { ...offer }
    });

    dialogRef.afterClosed().subscribe({
      next: (result) => {
        if (result) {
          this.offerService.updateOffer(offer.id, result);
        }
      },
      error: (error) => console.error('Error updating offer:', error)
    });
  }

  deleteOffer(offerId: number): void {
    if (confirm('Are you sure you want to delete this offer?')) {
      this.offerService.deleteOffer(offerId);
    }
  }

}
