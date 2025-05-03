import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-add-item-dialog',
  standalone: true,
  imports: [
    CommonModule,
    MatDialogModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    FormsModule
  ],
  templateUrl: './add-item-dialog.component.html',
  styleUrl: './add-item-dialog.component.scss'
})
export class AddItemDialogComponent {
  name: string = '';
  unitPrice: number = 0;
  private readonly dialogRef = inject(MatDialogRef<AddItemDialogComponent>);

  createItem(): void {
    if (!this.name || this.unitPrice <= 0) {
      return;
    }
    
    this.dialogRef.close({
      name: this.name,
      unitPrice: this.unitPrice
    });
  }
}
