package com.library.management.config;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.library.management.entity.Book;
import com.library.management.entity.LibraryUser;
import com.library.management.entity.Member;
import com.library.management.repository.BookRepository;
import com.library.management.repository.LibraryUserRepository;
import com.library.management.repository.MemberRepository;

@Configuration
public class DevDataInitializer {

    @Bean
    @SuppressWarnings("unused")
    CommandLineRunner seed(
            LibraryUserRepository users,
            BookRepository books,
            MemberRepository members,
            PasswordEncoder encoder,
            @Value("${app.seed-demo:true}") boolean seedDemo) {

        return args -> {
            if (users.findByUsername("librarian").isEmpty()) {
                users.save(new LibraryUser(
                        "librarian",
                        encoder.encode(System.getenv().getOrDefault("LIBRARIAN_PASSWORD", "ChangeMe123!")),
                        "LIBRARIAN"
                ));
            }

            if (seedDemo && books.count() == 0) {
                Book book = new Book();
                book.setTitle("Clean Code");
                book.setAuthor("Robert C. Martin");
                book.setIsbn("9780132350884");
                book.setCategory("Programming");
                book.setPublisher("Prentice Hall");
                book.setPublicationYear(2008);
                book.setTotalCopies(4);
                book.setAvailableCopies(4);
                book.setShelfLocation("A-01");
                books.save(book);
            }

            if (seedDemo && members.count() == 0) {
                Member member = new Member();
                member.setFullName("Demo Student");
                member.setEmail("student@example.com");
                member.setPhone("9000000000");
                member.setAddress("Demo address");
                member.setRegistrationDate(LocalDate.now());
                member.setActive(true);
                members.save(member);
            }
        };
    }
}
