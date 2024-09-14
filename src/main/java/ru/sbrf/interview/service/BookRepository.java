package ru.sbrf.interview.service;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<DemoServiceImpl.Book, Long> {
    boolean existsByTitle(String title);
}
