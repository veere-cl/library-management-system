package com.library.management.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.library.management.entity.Book;
import com.library.management.entity.BorrowRecord;
import com.library.management.entity.Member;
import com.library.management.exception.BusinessException;
import com.library.management.exception.NotFoundException;
import com.library.management.repository.BookRepository;
import com.library.management.repository.BorrowRecordRepository;
import com.library.management.repository.MemberRepository;

@Service
public class BorrowService {

    private final BorrowRecordRepository records;
    private final BookRepository books;
    private final MemberRepository members;

    @Value("${library.borrow-days:14}")
    private int borrowDays;

    @Value("${library.daily-fine:2.00}")
    private BigDecimal dailyFine;

    public BorrowService(BorrowRecordRepository records, BookRepository books, MemberRepository members) {
        this.records = records;
        this.books = books;
        this.members = members;
    }

    @Transactional
    public BorrowRecord issue(Long bookId, Long memberId) {
        Book book = books.findById(bookId)
                .orElseThrow(() -> new NotFoundException("Book not found."));
        Member member = members.findById(memberId)
                .orElseThrow(() -> new NotFoundException("Member not found."));

        if (!member.isActive()) {
            throw new BusinessException("Inactive members cannot borrow books.");
        }

        if (book.getAvailableCopies() < 1) {
            throw new BusinessException("No copies are available.");
        }

        book.setAvailableCopies(book.getAvailableCopies() - 1);

        BorrowRecord record = new BorrowRecord();
        record.setBook(book);
        record.setMember(member);
        record.setIssueDate(LocalDate.now());
        record.setDueDate(LocalDate.now().plusDays(borrowDays));
        record.setStatus("ISSUED");
        record.setFine(BigDecimal.ZERO);

        return records.save(record);
    }

    @Transactional
    public BorrowRecord returnBook(Long id) {
        BorrowRecord record = records.findById(id)
                .orElseThrow(() -> new NotFoundException("Borrow record not found."));

        if (!"ISSUED".equals(record.getStatus())) {
            throw new BusinessException("This borrowing record has already been returned.");
        }

        LocalDate today = LocalDate.now();
        long days = Math.max(0, ChronoUnit.DAYS.between(record.getDueDate(), today));

        record.setReturnDate(today);
        record.setStatus("RETURNED");
        record.setFine(dailyFine.multiply(BigDecimal.valueOf(days)));

        Book book = record.getBook();
        if (book.getAvailableCopies() >= book.getTotalCopies()) {
            throw new BusinessException("Copy count integrity check failed.");
        }
        book.setAvailableCopies(book.getAvailableCopies() + 1);

        return records.save(record);
    }

    public List<BorrowRecord> issued() {
        return records.findByStatus("ISSUED");
    }

    public List<BorrowRecord> overdue() {
        return records.findByStatusAndDueDateBefore("ISSUED", LocalDate.now());
    }

    public List<BorrowRecord> history(Long memberId) {
        return records.findByMemberIdOrderByIssueDateDesc(memberId);
    }

    public List<BorrowRecord> all() {
        return records.findAll();
    }

    public long issuedCount() {
        return records.countByStatus("ISSUED");
    }
}
