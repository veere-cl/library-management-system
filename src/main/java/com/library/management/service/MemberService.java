package com.library.management.service;

import com.library.management.entity.Member;
import com.library.management.exception.BusinessException;
import com.library.management.exception.NotFoundException;
import com.library.management.repository.BorrowRecordRepository;
import com.library.management.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class MemberService {

    private final MemberRepository members;
    private final BorrowRecordRepository borrows;

    public MemberService(MemberRepository members, BorrowRecordRepository borrows) {
        this.members = members;
        this.borrows = borrows;
    }

    public List<Member> all(String query) {
        if (query == null || query.isBlank()) {
            return members.findAll();
        }
        return members.findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(query, query);
    }

    public Member get(Long id) {
        return members.findById(id)
                .orElseThrow(() -> new NotFoundException("Member not found: " + id));
    }

    public Member save(Member member) {
        if (member.getRegistrationDate() == null) {
            member.setRegistrationDate(LocalDate.now());
        }
        return members.save(member);
    }

    @Transactional
    public void delete(Long id) {
        get(id);

        if (borrows.existsByMemberIdAndStatus(id, "ISSUED")) {
            throw new BusinessException("Cannot delete a member with active borrowing records.");
        }

        members.deleteById(id);
    }
}
