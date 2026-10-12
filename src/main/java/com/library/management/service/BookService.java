package com.library.management.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.library.management.entity.Book;
import com.library.management.exception.BusinessException;
import com.library.management.exception.NotFoundException;
import com.library.management.repository.BookRepository;
import com.library.management.repository.BorrowRecordRepository;

@Service
public class BookService {

    private final BookRepository books;
    private final BorrowRecordRepository borrows;

    public BookService(BookRepository books, BorrowRecordRepository borrows) {
        this.books = books;
        this.borrows = borrows;
    }

    public List<Book> all(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return books.findAll();
        }
        return books.search(keyword.trim());
    }

    public Book get(Long id) {
        return books.findById(id)
                .orElseThrow(() -> new NotFoundException("Book not found: " + id));
    }

    @Transactional
    public Book save(Book book) {
        if (book.getAvailableCopies() == null) {
            book.setAvailableCopies(book.getTotalCopies());
        }

        if (book.getTotalCopies() == null
                || book.getAvailableCopies() < 0
                || book.getAvailableCopies() > book.getTotalCopies()) {
            throw new BusinessException("Available copies must be between zero and total copies.");
        }

        if (book.getId() != null) {
            Book existingBook = get(book.getId());
            int issuedCopies = existingBook.getTotalCopies() - existingBook.getAvailableCopies();

            if (book.getTotalCopies() < issuedCopies) {
                throw new BusinessException("Total copies cannot be lower than copies currently issued.");
            }

            book.setAvailableCopies(book.getTotalCopies() - issuedCopies);
        }

        return books.save(book);
    }

    @Transactional
    public void delete(Long id) {
        get(id);

        if (borrows.existsByBookIdAndStatus(id, "ISSUED")) {
            throw new BusinessException("Cannot delete a book with active borrowing records.");
        }

        books.deleteById(id);
    }
}
