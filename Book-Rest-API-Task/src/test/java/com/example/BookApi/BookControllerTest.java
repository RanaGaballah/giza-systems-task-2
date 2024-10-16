package com.example.BookApi;

import com.example.BookApi.classes.Book;
import com.example.BookApi.controllers.BookController;
import com.example.BookApi.exceptionHandler.BookNotFoundException;
import com.example.BookApi.services.BookService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
public class BookControllerTest {

    @Mock
    BookService bookService;

    @InjectMocks
    BookController bookController;

    private static Book AlgorithmsBook;
    private static Book DataStructuresBook;
    private static Book JavaBook;

    @BeforeAll
    public static void BeforeTestsSetup() {
        AlgorithmsBook = new Book(1L, "Algorithms", "Thomas H. Cormen", 200.0, 1990);
        DataStructuresBook = new Book(2L, "Data Structures", "Thomas H. Cormen", 250.0, 1995);
        JavaBook = new Book(3L, "Java", "James Gosling", 300.0, 1999);
    }

    @Test
    public void getAllBooksTest() {
        List<Book> books = List.of(AlgorithmsBook, DataStructuresBook, JavaBook);
        when(bookService.getAllBooks()).thenReturn(books);
        List<Book> BooksFound = bookController.getAllBooks();
        assertEquals(books, BooksFound);
//        assertEquals(3, BooksFound.size());
    }

    @Test
    public void getBookByIdTest_IsFound() {
        when(bookService.getBookById(1L)).thenReturn(Optional.of(AlgorithmsBook));
        ResponseEntity<?> response = bookController.getBookById(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        Book bookFound = (Book) response.getBody();
        assertEquals(AlgorithmsBook, bookFound);
    }

    @Test
    public void getBookByIdTest_NotFound() {
        when(bookService.getBookById(99L)).thenReturn(Optional.empty());
        Exception exception = assertThrows(BookNotFoundException.class, () -> bookController.getBookById(99L));
        assertEquals("Book with ID 99 not found", exception.getMessage());
    }

    @Test
    public void testAddBook() throws Exception {
        Book SpringBootBook = new Book(4L, "Spring Boot", "Craig Walls", 400.0, 2000);
        when(bookService.addBook(any(Book.class))).thenReturn(SpringBootBook);
        ResponseEntity<Book> response = bookController.addBook(SpringBootBook);
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Spring Boot", response.getBody().getTitle());
        assertEquals(SpringBootBook, response.getBody());
    }

    @Test
    public void testUpdateBook_Success() {
        AlgorithmsBook.setTitle("Algorithms_Updated");
        AlgorithmsBook.setBookPrice(250.9);
        when(bookService.updateBook(eq(1L), any(Book.class))).thenReturn(AlgorithmsBook);
        ResponseEntity<?> response = bookController.updateBook(1L, AlgorithmsBook);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertInstanceOf(Book.class, response.getBody());
        Book resultBook = (Book) response.getBody();
        assertEquals(AlgorithmsBook, resultBook);
        assertEquals("Algorithms_Updated", resultBook.getTitle());
    }

    @Test
    public void testUpdateBook_NotFound() {
        when(bookService.updateBook(eq(1000L), any(Book.class))).thenThrow(new BookNotFoundException(1000L));
        Exception exception = assertThrows(BookNotFoundException.class, () -> bookController.updateBook(1000L, AlgorithmsBook));
        assertEquals("Book with ID 1000 not found", exception.getMessage());
    }

    @Test
    public void testDeleteBook_Success() {
        doNothing().when(bookService).deleteBook(1L);
        ResponseEntity<?> response = bookController.deleteBook(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testDeleteBook_NotFound() {
        doThrow(new BookNotFoundException(99L)).when(bookService).deleteBook(99L);
        Exception exception = assertThrows(BookNotFoundException.class, () -> bookController.deleteBook(99L));
        assertEquals("Book with ID 99 not found", exception.getMessage());
    }
}
