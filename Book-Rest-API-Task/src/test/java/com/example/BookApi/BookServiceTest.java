package com.example.BookApi;

import com.example.BookApi.classes.Book;
import com.example.BookApi.exceptionHandler.BookNotFoundException;
import com.example.BookApi.repositories.BookRepository;
import com.example.BookApi.services.BookService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataAccessException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    private static Book AlgorithmsBook;
    private static Book DataStructuresBook;
    private static Book JavaBook;

    @BeforeAll
    public static void setupBooks() {
        AlgorithmsBook = new Book(1L, "Algorithms", "Thomas H. Cormen", 200.0, 1990);
        DataStructuresBook = new Book(2L, "Data Structures", "Thomas H. Cormen", 250.0, 1995);
        JavaBook = new Book(3L, "Java", "James Gosling", 300.0, 1999);
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllBooks_Success() {
        List<Book> books = List.of(AlgorithmsBook, DataStructuresBook, JavaBook);
        when(bookRepository.findAll()).thenReturn(books);

        List<Book> result = bookService.getAllBooks();
        assertNotNull(result);
        assertEquals(AlgorithmsBook, result.get(0));
        assertEquals(DataStructuresBook, result.get(1));
        assertEquals(JavaBook, result.get(2));
        verify(bookRepository, times(1)).findAll();
    }

    @Test
    void testGetAllBooks_DataAccessException() {
        when(bookRepository.findAll()).thenThrow(new DataAccessException("Error") {
        });

        Exception exception = assertThrows(RuntimeException.class, () -> {
            bookService.getAllBooks();
        });

        assertEquals("Database error occurred while fetching books.", exception.getMessage());
        verify(bookRepository, times(1)).findAll();
    }

    @Test
    void testGetBookById_BookExists() {
        when(bookRepository.existsById(1L)).thenReturn(true);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(AlgorithmsBook));
        Optional<Book> result = bookService.getBookById(1L);
        assertTrue(result.isPresent());
        assertEquals(AlgorithmsBook, result.get());
        verify(bookRepository, times(1)).findById(1L);
    }

    @Test
    void testGetBookById_BookNotFound() {
        when(bookRepository.existsById(99L)).thenReturn(false);

        Exception exception = assertThrows(BookNotFoundException.class, () -> {
            bookService.getBookById(99L);
        });

        assertEquals("Book with ID 99 not found", exception.getMessage());
        verify(bookRepository, times(0)).findById(99L);
    }

    @Test
    void testAddBook_Success() {
        when(bookRepository.save(any(Book.class))).thenReturn(DataStructuresBook);
        Book result = bookService.addBook(DataStructuresBook);

        assertNotNull(result);
        assertEquals(result, DataStructuresBook);
        verify(bookRepository, times(1)).save(DataStructuresBook);
    }

    @Test
    void testAddBook_DataAccessException() {
        when(bookRepository.save(any(Book.class))).thenThrow(new DataAccessException("Error") {
        });

        Exception exception = assertThrows(RuntimeException.class, () -> {
            bookService.addBook(AlgorithmsBook);
        });

        assertEquals("Database error occurred while adding the book.", exception.getMessage());
        verify(bookRepository, times(1)).save(AlgorithmsBook);
    }

    @Test
    void testUpdateBook_Success() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(AlgorithmsBook));
        when(bookRepository.save(any(Book.class))).thenReturn(AlgorithmsBook);

        AlgorithmsBook.setTitle("Algorithms_Updated");
        AlgorithmsBook.setBookPrice(250.9);

        Book result = bookService.updateBook(1L, AlgorithmsBook);

        assertNotNull(result);
        assertEquals("Algorithms_Updated", result.getTitle());
        assertEquals(250.9, result.getBookPrice());
        verify(bookRepository, times(1)).save(AlgorithmsBook);
    }

    @Test
    void testUpdateBook_BookNotFound() {
        when(bookRepository.findById(1000L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(BookNotFoundException.class, () -> {
            bookService.updateBook(1000L, AlgorithmsBook);
        });

        assertEquals("Book with ID 1000 not found", exception.getMessage());
        verify(bookRepository, times(0)).save(any(Book.class));
    }

    @Test
    void testDeleteBook_Success() {
        when(bookRepository.existsById(1L)).thenReturn(true);
        doNothing().when(bookRepository).deleteById(1L);

        bookService.deleteBook(1L);

        verify(bookRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteBook_BookNotFound() {
        when(bookRepository.existsById(99L)).thenReturn(false);

        Exception exception = assertThrows(BookNotFoundException.class, () -> {
            bookService.deleteBook(99L);
        });

        assertEquals("Book with ID 99 not found", exception.getMessage());
        verify(bookRepository, times(0)).deleteById(99L);
    }
}
