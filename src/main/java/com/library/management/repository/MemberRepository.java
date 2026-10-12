package com.library.management.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.library.management.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

    List<Member> findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String name, String email);

    boolean existsByEmail(String email);
}
