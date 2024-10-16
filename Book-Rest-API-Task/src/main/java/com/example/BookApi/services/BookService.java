package com.example.BookApi.services;

import com.example.BookApi.classes.Book;
import com.example.BookApi.exceptionHandler.BookNotFoundException;
import com.example.BookApi.repositories.BookRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {
    private final BookRepository bookRepository;

    @Autowired
    BookService(BookRepository bookRepository) {
            this.bookRepository = bookRepository;
    }
    public List<Book> getAllBooks() {
        try {
            return bookRepository.findAll();
        } catch (DataAccessException e) {
            throw new RuntimeException("Database error occurred while fetching books.", e);
        }
    }

    public Optional<Book> getBookById(Long id) {
        try {
            if (bookRepository.existsById(id))
                return bookRepository.findById(id);
            else
                throw new BookNotFoundException(id);
        } catch (DataAccessException e) {
            throw new RuntimeException("Database error occurred while fetching the book.", e);
        }
    }

    @Transactional
    public Book addBook(Book book) {
        try {
            return bookRepository.save(book);
        } catch (DataAccessException e) {
            throw new RuntimeException("Database error occurred while adding the book.", e);
        }
    }

    @Transactional
    public Book updateBook(Long id, Book updatedBook) {
        try {
            Optional<Book> existingBookOptional = bookRepository.findById(id);
            if (existingBookOptional.isPresent()) {
                Book existingBook = existingBookOptional.get();
                existingBook.setTitle(updatedBook.getTitle());
                existingBook.setAuthor(updatedBook.getAuthor());
                existingBook.setBookPrice(updatedBook.getBookPrice());
                existingBook.setYearPublished(updatedBook.getYearPublished());
                return bookRepository.save(existingBook);
            } else {
                throw new BookNotFoundException(id);
            }
        } catch (DataAccessException e) {
            throw new RuntimeException("Database error occurred while updating the book.", e);
        }
    }

    @Transactional
    public void deleteBook(Long id) {
        try {
            if (bookRepository.existsById(id))
                bookRepository.deleteById(id);
            else
                throw new BookNotFoundException(id);
        } catch (DataAccessException e) {
            throw new RuntimeException("Database error occurred while deleting the book.", e);
        }
    }
}
