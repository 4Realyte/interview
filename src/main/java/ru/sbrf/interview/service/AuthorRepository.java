package ru.sbrf.interview.service;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<DemoService.Author, Long> {
}
