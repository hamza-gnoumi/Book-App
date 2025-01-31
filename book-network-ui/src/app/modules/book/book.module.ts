import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { BookRoutingModule } from './book-routing.module';
import { MyBooksComponent } from './pages/my-books/my-books.component';
import { FormsModule } from '@angular/forms';
import { BookCardComponent } from './components/book-card/book-card.component';
import { MenuComponent } from './components/menu/menu.component';
import { RatingComponent } from './components/rating/rating.component';
import { BookListComponent } from './pages/book-list/book-list.component';
import { MainComponent } from './pages/main/main.component';
import { ManageBookComponent } from './pages/manage-book/manage-book.component';
import { BorrowedBookListComponent } from './pages/borrowed-book-list/borrowed-book-list.component';
import { ReturnBooksComponent } from './pages/return-books/return-books.component';


@NgModule({
  declarations: [
    MainComponent,
    MenuComponent,
    BookListComponent,
    BookCardComponent,
    MyBooksComponent,
    RatingComponent,
    ManageBookComponent,
    BorrowedBookListComponent,
    ReturnBooksComponent
  ],
  imports: [
    CommonModule,
    BookRoutingModule,
    FormsModule
  ]
})
export class BookModule { }
