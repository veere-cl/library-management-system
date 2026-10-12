package com.library.management.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.library.management.entity.BorrowRecord;

public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {

    List<BorrowRecord> findByStatus(String status);

    List<BorrowRecord> findByMemberIdOrderByIssueDateDesc(Long memberId);

    List<BorrowRecord> findByStatusAndDueDateBefore(String status, LocalDate date);

    boolean existsByBookIdAndStatus(Long bookId, String status);

    boolean existsByMemberIdAndStatus(Long memberId, String status);

    long countByStatus(String status);

    List<BorrowRecord> findTop8ByOrderByIssueDateDesc();
}
