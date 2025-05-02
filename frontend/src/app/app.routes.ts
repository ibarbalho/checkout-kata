import { Routes } from '@angular/router';
import { ProductListComponent } from './product-list/product-list.component';
import { CartComponent } from './cart/cart.component';
import { OffersComponent } from './offers/offers.component';
import { ItemsComponent } from './items/items.component';

export const routes: Routes = [
    { path: '', redirectTo: '/productlist', pathMatch: 'full' },
    { path: 'productlist', component: ProductListComponent },
    { path: 'offers', component: OffersComponent },
    { path: 'items', component: ItemsComponent },
    { path: 'cart', component: CartComponent }
  ];
