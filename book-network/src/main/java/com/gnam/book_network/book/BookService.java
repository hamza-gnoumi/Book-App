package com.gnam.book_network.book;

import com.gnam.book_network.common.PageResponse;
import com.gnam.book_network.exception.OperationNotPermittedException;
import com.gnam.book_network.file.FileStorageService;
import com.gnam.book_network.history.BookTransactionHistory;
import com.gnam.book_network.history.BookTransactionHistoryRepository;
import com.gnam.book_network.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final BookTransactionHistoryRepository bookTransactionHistoryRepository;
    private final FileStorageService fileStorageService;

    public Integer save(BookRequest request, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Book book =bookMapper.toBook(request);
        book.setOwner(user);
        return bookRepository.save(book).getId();
    }

    public BookResponse findById(Integer bookId) {

        return bookRepository.findById(bookId)
                .map(bookMapper::toBookResponse)
                .orElseThrow(()-> new EntityNotFoundException("No Book found with the ID : "+bookId));
    }

    public PageResponse<BookResponse> findAllBooks(int page, int size, Authentication connectedUser) {
         User user = ((User) connectedUser.getPrincipal());
         System.out.println(user.getId());
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Book> books = bookRepository.findAllDisplayableBooks(pageable, user.getId());
        List<BookResponse> booksResponse = books.stream()
                .map(bookMapper::toBookResponse)
                .toList();
        return new PageResponse<>(
                booksResponse,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast()
        );
    }


    public PageResponse<BookResponse> findAllBooksByOwner(int page, int size, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Book> books = bookRepository.findAll(BookSpecification.withOwnerId(user.getId()),pageable);
        List<BookResponse> bookResponses=books.stream()
                .map(bookMapper::toBookResponse)
                .toList();
        return new PageResponse<>(
                bookResponses,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast()

        );
    }

    public PageResponse<BorrowedBookResponse> findAllBorrowedBooks(int page, int size, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<BookTransactionHistory> allBorrowedBooks = bookTransactionHistoryRepository
                .findAllBorrowedBooks(pageable,user.getId());

        List<BorrowedBookResponse> bookResponses=allBorrowedBooks.stream()
                .map(bookMapper::toBorrowedBookResponse)
                .toList();
        return new PageResponse<>(
                bookResponses,
                allBorrowedBooks.getNumber(),
                allBorrowedBooks.getSize(),
                allBorrowedBooks.getTotalElements(),
                allBorrowedBooks.getTotalPages(),
                allBorrowedBooks.isFirst(),
                allBorrowedBooks.isLast()

        );
    }

    public PageResponse<BorrowedBookResponse> findAllReturnedBooks(int page, int size, Authentication connectedUser) {

        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<BookTransactionHistory> allBorrowedBooks = bookTransactionHistoryRepository
                .findAllReturnedBooks(pageable,user.getId());

        List<BorrowedBookResponse> bookResponses=allBorrowedBooks.stream()
                .map(bookMapper::toBorrowedBookResponse)
                .toList();
        return new PageResponse<>(
                bookResponses,
                allBorrowedBooks.getNumber(),
                allBorrowedBooks.getSize(),
                allBorrowedBooks.getTotalElements(),
                allBorrowedBooks.getTotalPages(),
                allBorrowedBooks.isFirst(),
                allBorrowedBooks.isLast()

        );
    }

    public Integer updateShareableStatus(Integer bookId, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Book book=bookRepository.findById(bookId)
                .orElseThrow(()-> new EntityNotFoundException("No Book found with the ID: "+bookId));

        if (!Objects.equals(book.getOwner().getId(), user.getId())){
            throw new OperationNotPermittedException("You cannot update others books shareable status");
        }
        book.setShareable(!book.isShareable());
        bookRepository.save(book);
        return bookId;
    }

    public Integer updateArchivedStatus(Integer bookId, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Book book=bookRepository.findById(bookId)
                .orElseThrow(()-> new EntityNotFoundException("No Book found with the ID: "+bookId));

        if (!Objects.equals(book.getOwner().getId(), user.getId())){
            throw new OperationNotPermittedException("You cannot update others books archived status");
        }
        book.setArchived(!book.isArchived());
        bookRepository.save(book);
        return bookId;
    }


    public Integer borrowBook(Integer bookId, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Book book=bookRepository.findById(bookId)
                .orElseThrow(()-> new EntityNotFoundException("No Book found with the ID: "+bookId));
        if (book.isArchived() || !book.isShareable()){
            throw new OperationNotPermittedException("The Request Book cannot be borrowed since it is archived or not shareable");
        }
        if (Objects.equals(book.getOwner().getId(), user.getId())){
            throw new OperationNotPermittedException("You cannot borrow your own book");
        }
        final boolean isAlreadyBorrowed=
                bookTransactionHistoryRepository.isAlreadyBorrowedByUser(bookId,user.getId());
        if (isAlreadyBorrowed){
            throw new OperationNotPermittedException("The Request Book is already borrowed");
        }
        BookTransactionHistory history=
                BookTransactionHistory.builder()
                        .user(user)
                        .book(book)
                        .returned(false)
                        .returnApproved(false)
                        .build();
        return bookTransactionHistoryRepository.save(history).getId();
    }

    public Integer returnBorrowedBook(Integer bookId, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Book book=bookRepository.findById(bookId)
                .orElseThrow(()-> new EntityNotFoundException("No Book found with the ID: "+bookId));

        if (book.isArchived() || !book.isShareable()){
            throw new OperationNotPermittedException("The Request Book cannot be borrowed since it is archived or not shareable");
        }
        if (Objects.equals(book.getOwner().getId(), user.getId())){
            throw new OperationNotPermittedException("You cannot borrow or return your own book");
        }
        BookTransactionHistory history =
                bookTransactionHistoryRepository.findByBookIdAndUserId(bookId,user.getId())
                        .orElseThrow(()->
                                        new OperationNotPermittedException("You did not borrow this Book"));

        history.setReturned(true);

        return bookTransactionHistoryRepository.save(history).getId();
    }

    public Integer approveReturnBorrowedBook(Integer bookId, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Book book=bookRepository.findById(bookId)
                .orElseThrow(()-> new EntityNotFoundException("No Book found with the ID: "+bookId));

        if (book.isArchived() || !book.isShareable()){
            throw new OperationNotPermittedException("The Request Book cannot be borrowed since it is archived or not shareable");
        }
        if (!Objects.equals(book.getOwner().getId(), user.getId())){
            throw new OperationNotPermittedException("You cannot return a book that you do not own");
        }
        BookTransactionHistory history =
                bookTransactionHistoryRepository.findByBookIdAndOwnerId(user.getId(),bookId)
                        .orElseThrow(()->
                                new OperationNotPermittedException("The book is not returned yet. You cannot approve its return"));

        history.setReturnApproved(true);
        return bookTransactionHistoryRepository.save(history).getId();
    }

    public void uploadBookCoverPicture(MultipartFile file, Authentication connectedUser, Integer bookId) {
        User user = (User) connectedUser.getPrincipal();
        Book book=bookRepository.findById(bookId)
                .orElseThrow(()-> new EntityNotFoundException("No Book found with the ID: "+bookId));
        var bookCover=fileStorageService.saveFile(file,user.getId());
        book.setBookCover(bookCover);
        bookRepository.save(book);

    }
}
