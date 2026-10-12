package com.library.management.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.library.management.entity.Book;
import com.library.management.entity.Member;
import com.library.management.service.BookService;
import com.library.management.service.BorrowService;
import com.library.management.service.MemberService;

import jakarta.validation.Valid;

@Controller
public class PageController {

    private final BookService books;
    private final MemberService members;
    private final BorrowService borrows;

    public PageController(BookService books, MemberService members, BorrowService borrows) {
        this.books = books;
        this.members = members;
        this.borrows = borrows;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/")
    public String dashboard(Model model) {
        model.addAttribute("bookCount", books.all(null).size());
        model.addAttribute("memberCount", members.all(null).size());
        model.addAttribute("issuedCount", borrows.issuedCount());
        model.addAttribute("availableCount", books.all(null).stream().mapToLong(Book::getAvailableCopies).sum());
        model.addAttribute("overdueCount", borrows.overdue().size());
        model.addAttribute("recent", borrows.all().stream().limit(8).toList());
        return "dashboard";
    }

    @GetMapping("/books")
    public String bookList(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("books", books.all(q));
        model.addAttribute("q", q);
        return "books";
    }

    @GetMapping("/books/new")
    public String newBook(Model model) {
        model.addAttribute("book", new Book());
        return "book-form";
    }

    @GetMapping("/books/{id}/edit")
    public String editBook(@PathVariable Long id, Model model) {
        model.addAttribute("book", books.get(id));
        return "book-form";
    }

    @PostMapping("/books/save")
    public String saveBook(@ModelAttribute("book") @Valid Book book, BindingResult errors, Model model) {
        if (errors.hasErrors()) {
            return "book-form";
        }

        books.save(book);
        return "redirect:/books";
    }

    @PostMapping("/books/{id}/delete")
    public String deleteBook(@PathVariable Long id) {
        books.delete(id);
        return "redirect:/books";
    }

    @GetMapping("/members")
    public String memberList(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("members", members.all(q));
        model.addAttribute("q", q);
        return "members";
    }

    @GetMapping("/members/new")
    public String newMember(Model model) {
        model.addAttribute("member", new Member());
        return "member-form";
    }

    @GetMapping("/members/{id}/edit")
    public String editMember(@PathVariable Long id, Model model) {
        model.addAttribute("member", members.get(id));
        return "member-form";
    }

    @PostMapping("/members/save")
    public String saveMember(@ModelAttribute("member") @Valid Member member, BindingResult errors) {
        if (errors.hasErrors()) {
            return "member-form";
        }

        members.save(member);
        return "redirect:/members";
    }

    @PostMapping("/members/{id}/delete")
    public String deleteMember(@PathVariable Long id) {
        members.delete(id);
        return "redirect:/members";
    }

    @GetMapping("/borrow")
    public String borrowPage(Model model) {
        model.addAttribute("books", books.all(null));
        model.addAttribute("members", members.all(null));
        model.addAttribute("issued", borrows.issued());
        return "borrow";
    }

    @PostMapping("/borrow/issue")
    public String issue(@RequestParam Long bookId, @RequestParam Long memberId) {
        borrows.issue(bookId, memberId);
        return "redirect:/borrow?issued";
    }

    @PostMapping("/borrow/{id}/return")
    public String returnBook(@PathVariable Long id) {
        borrows.returnBook(id);
        return "redirect:/borrow?returned";
    }

    @GetMapping("/reports")
    public String reports(Model model) {
        model.addAttribute("records", borrows.all());
        model.addAttribute("overdue", borrows.overdue());
        return "reports";
    }
}
