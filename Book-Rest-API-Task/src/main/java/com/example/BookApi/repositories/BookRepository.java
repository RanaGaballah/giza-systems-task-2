package com.example.BookApi.repositories;

import com.example.BookApi.classes.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}