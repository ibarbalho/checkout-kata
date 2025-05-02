import { Routes } from '@angular/router';
import { ProductListComponent } from './product-list/product-list.component';
import { CartComponent } from './cart/cart.component';

export const routes: Routes = [
    { path: '', redirectTo: '/productlist', pathMatch: 'full' },
    { path: 'productlist', component: ProductListComponent },
    { path: 'items', component: ProductListComponent },
    { path: 'checkout', component: CartComponent }
  ];
