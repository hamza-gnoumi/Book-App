import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { BookRequest } from 'src/app/services/models';
import { BookService } from 'src/app/services/services';

@Component({
  selector: 'app-manage-book',
  templateUrl: './manage-book.component.html',
  styleUrls: ['./manage-book.component.scss']
})
export class ManageBookComponent implements OnInit {

  errorMsg: Array<string> = [];
  selectedPicture: string | undefined;
  selectedBookCover: any;
  bookRequest: BookRequest = {
    authorName: '',
    isbn: '',
    synopsis: '',
    title: ''
  };

  constructor(
    private bookService: BookService,
    private router: Router,
    private activatedRoute: ActivatedRoute
  ) { }

  ngOnInit(): void {
    const bookId = this.activatedRoute.snapshot.params['bookId'];
    if (bookId) {
      this.bookService.findById({
        book_id: bookId
      }).subscribe({
        next: (book) => {
          this.bookRequest = {
            id: book.id,
            title: book.title as string,
            authorName: book.authorName as string,
            isbn: book.isbn as string,
            synopsis: book.synopsis as string,
            shareable: book.shareable
          };
          this.selectedPicture = 'data:image/jpg;base64,' + book.cover;
        }
      });
    }
  }

  onFileSelected(file: any) {
    this.selectedBookCover = file.target.files[0];
    if (this.selectedBookCover) {
      const reader: FileReader = new FileReader();
      reader.onload = () => {
        this.selectedPicture = reader.result as string;
      }
      reader.readAsDataURL(this.selectedBookCover);
    }
  }

  saveBook() {
    this.bookService.saveBook({
      body: this.bookRequest
    }).subscribe({
      next: (bookId) => {
        this.bookService.uploadBookCoverPicture({
          book_id: bookId,
          body: {
            file: this.selectedBookCover
          }
        }).subscribe({
          next: () => {
            this.router.navigate(['/books/my-books']);
          }
        })
      },
      error: (err) => {
        this.errorMsg = err.error.validationErrors;
      }
    })
  }
}
