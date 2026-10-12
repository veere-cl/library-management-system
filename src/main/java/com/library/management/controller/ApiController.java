package com.library.management.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.library.management.entity.Book;
import com.library.management.entity.BorrowRecord;
import com.library.management.entity.Member;
import com.library.management.service.BookService;
import com.library.management.service.BorrowService;
import com.library.management.service.MemberService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final BookService books;
    private final MemberService members;
    private final BorrowService borrows;

    public ApiController(BookService books, MemberService members, BorrowService borrows) {
        this.books = books;
        this.members = members;
        this.borrows = borrows;
    }

    @GetMapping("/books")
    public List<Book> books(@RequestParam(required = false) String keyword) {
        return books.all(keyword);
    }

    @GetMapping("/books/{id}")
    public Book book(@PathVariable Long id) {
        return books.get(id);
    }

    @PostMapping("/books")
    @ResponseStatus(HttpStatus.CREATED)
    public Book addBook(@Valid @RequestBody Book book) {
        return books.save(book);
    }

    @PutMapping("/books/{id}")
    public Book updateBook(@PathVariable Long id, @Valid @RequestBody Book book) {
        book.setId(id);
        return books.save(book);
    }

    @DeleteMapping("/books/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBook(@PathVariable Long id) {
        books.delete(id);
    }

    @GetMapping("/books/search")
    public List<Book> search(@RequestParam String keyword) {
        return books.all(keyword);
    }

    @GetMapping("/members")
    public List<Member> members(@RequestParam(required = false) String keyword) {
        return members.all(keyword);
    }

    @GetMapping("/members/{id}")
    public Member member(@PathVariable Long id) {
        return members.get(id);
    }

    @PostMapping("/members")
    @ResponseStatus(HttpStatus.CREATED)
    public Member addMember(@Valid @RequestBody Member member) {
        return members.save(member);
    }

    @PutMapping("/members/{id}")
    public Member updateMember(@PathVariable Long id, @Valid @RequestBody Member member) {
        member.setId(id);
        return members.save(member);
    }

    @DeleteMapping("/members/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMember(@PathVariable Long id) {
        members.delete(id);
    }

    @PostMapping("/borrow/issue")
    @ResponseStatus(HttpStatus.CREATED)
    public BorrowRecord issue(@RequestBody Map<String, Long> body) {
        return borrows.issue(body.get("bookId"), body.get("memberId"));
    }

    @PostMapping("/borrow/{id}/return")
    public BorrowRecord returnBook(@PathVariable Long id) {
        return borrows.returnBook(id);
    }

    @GetMapping("/borrow/issued")
    public List<BorrowRecord> issued() {
        return borrows.issued();
    }

    @GetMapping("/borrow/overdue")
    public List<BorrowRecord> overdue() {
        return borrows.overdue();
    }

    @GetMapping("/borrow/member/{memberId}")
    public List<BorrowRecord> history(@PathVariable Long memberId) {
        return borrows.history(memberId);
    }

    @GetMapping("/dashboard/stats")
    public Map<String, Object> stats() {
        List<Book> allBooks = books.all(null);
        List<Member> allMembers = members.all(null);

        return Map.of(
                "totalBooks", allBooks.size(),
                "totalMembers", allMembers.size(),
                "issuedBooks", borrows.issuedCount(),
                "availableCopies", allBooks.stream().mapToLong(Book::getAvailableCopies).sum(),
                "overdueBooks", borrows.overdue().size()
        );
    }
}
