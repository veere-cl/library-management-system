package com.library.management.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.library.management.entity.Book;

public interface BookRepository extends JpaRepository<Book, Long> {

    @Query("select b from Book b where lower(b.title) like lower(concat('%',:q,'%')) " +
            "or lower(b.author) like lower(concat('%',:q,'%')) " +
            "or lower(b.isbn) like lower(concat('%',:q,'%')) " +
            "or lower(b.category) like lower(concat('%',:q,'%'))")
    List<Book> search(@Param("q") String q);

    boolean existsByIsbn(String isbn);
}
