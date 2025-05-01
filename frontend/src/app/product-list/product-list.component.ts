import { Component, effect, OnInit } from '@angular/core';
import { NgFor, CurrencyPipe } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { ItemService } from '../services/item.service';

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [
    NgFor,
    CurrencyPipe,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatDividerModule
  ],
  templateUrl: './product-list.component.html',
  styleUrls: ['./product-list.component.scss']
})
export class ProductListComponent implements OnInit{

  constructor(private itemService: ItemService){
    effect(() => {
      console.log('>>>', this.itemService.items());
    });
  }  

  ngOnInit(): void {
    this.itemService.getAllItems();
  }

}


