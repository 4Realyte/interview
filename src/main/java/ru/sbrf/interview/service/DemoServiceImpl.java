package ru.sbrf.interview.service;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DemoServiceImpl implements DemoService {
    AuthorRepository repository;
    BookRepository bookRepository;

    @Override
    @Transactional
    public void saveAuthorWithBooks() {
        Author author = repository.findAll().stream().findFirst().orElseThrow();
        Book book = Book.builder()
                .title("War and Peace")
                .build();
        try {
            validateBookTitle(book.getTitle());
        } catch (IllegalArgumentException exception) {
            book.setTitle("short");
        }
        author.addBook(book);
        repository.save(author);
    }

    @Transactional
    public void validateBookTitle(String title) {
        if (title.length() > 5 || bookRepository.existsByTitle(title)) {
            throw new IllegalArgumentException();
        }
    }

    @Data
    @Entity
    @NoArgsConstructor
    @Table(name = "book")
    @AllArgsConstructor
    @Builder
    static class Book {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        @Column
        private String title;
        @ManyToOne
        @JoinColumn(name = "author_id")
        private Author author;
    }

    @Data
    @Entity
    @Table(name = "author")
    @NoArgsConstructor
    @AllArgsConstructor
    static class Author {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        @Column
        private String name;
        @Column
        private Integer age;
        @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<Book> books = new ArrayList<>();

        public void addBook(Book book) {
            books.add(book);
            book.setAuthor(this);
        }
    }

    @Service
    @RequiredArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    static class TransactionService {
        PlatformTransactionManager platformTransactionManager;

        public <T> T doInTransaction(Supplier<T> supplier) {
            TransactionTemplate transactionTemplate = new TransactionTemplate(platformTransactionManager);
            transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
            return transactionTemplate.execute(transactionStatus -> supplier.get());
        }

        public void doInTransaction(Runnable runnable) {
            doInTransaction(runnable, TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        }

        public void doInTransaction(Runnable runnable, int propagation) {
            TransactionTemplate transactionTemplate = new TransactionTemplate(platformTransactionManager);
            transactionTemplate.setPropagationBehavior(propagation);
            transactionTemplate.executeWithoutResult(transactionStatus -> runnable.run());
        }
    }
}
