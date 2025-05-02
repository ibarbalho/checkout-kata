import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { FormsModule } from '@angular/forms';
import { Offer } from '../services/offer.service';

@Component({
  selector: 'app-edit-offer-dialog',
  standalone: true,
  imports: [
    CommonModule,
    MatDialogModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    FormsModule
  ],
  templateUrl: './edit-offer-dialog.component.html',
  styleUrl: './edit-offer-dialog.component.scss'
})
export class EditOfferDialogComponent {
  constructor(@Inject(MAT_DIALOG_DATA) public data: Offer) {}
}
