package edu.unac.repository;

import edu.unac.domain.Book;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class BookRepositoryTest {
    private static BookRepository bookRepository;
    private static Connection connection;
    @BeforeAll
    static void setUp() throws SQLException {
        connection = DriverManager.getConnection("jdbc:h2:mem:testDb");
        bookRepository = new BookRepository(connection);

        connection.prepareStatement(
                "CREATE TABLE books (id VARCHAR(255) PRIMARY KEY, title VARCHAR(255), borrowed_by VARCHAR(255))"
        ).executeUpdate();
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.prepareStatement(
                "TRUNCATE TABLE books"
        ).executeUpdate();
    }

    @Test
    public void saveBook() throws SQLException {
        Book book = new Book("1", "Clean Code");
        bookRepository.save(book);

        Optional<Book> bookOptional = bookRepository.findById("1");
        assertTrue(bookOptional.isPresent());
    }

    @Test
    public void saveBookNotFound() throws SQLException {
        Book book = new Book("1", "Clean Code");
        bookRepository.save(book);

        Optional<Book> bookOptional = bookRepository.findById("2");
        assertFalse(bookOptional.isPresent());
    }

    @Test
    public void findAll() throws SQLException {
        Book book = new Book("1", "Clean Code");
        bookRepository.save(book);

        Book bookTwo = new Book("2", "Introduction to Algorithms");
        bookRepository.save(bookTwo);

        List<Book> books = bookRepository.findAll();
        assertEquals(2, books.size());
    }

    @Test
    public void findByTitle() throws SQLException {
        Book book = new Book("3", "Software Usability");
        bookRepository.save(book);

        Optional<Book> bookOptional = bookRepository.findByTitle("Software Usability");
        assertTrue(bookOptional.isPresent());
        assertEquals("Software Usability", bookOptional.get().getTitle());
    }

    @Test
    public void findByTitleNotFound() throws SQLException {
        Book book = new Book("4", "Software Usability 2");
        bookRepository.save(book);

        Optional<Book> bookOptional = bookRepository.findByTitle("Software Usability");
        assertFalse(bookOptional.isPresent());
    }

    @Test
    void deleteBookTest() throws SQLException {
        bookRepository.save(new Book("12", "The Cucumber Book"));
        Optional<Book> optionalBook = bookRepository.findById("12");
        assertTrue(optionalBook.isPresent());

        bookRepository.deleteById("12");//optionalBook.get().getId()
        Optional<Book> deletedBook = bookRepository.findById("12");
        assertFalse(deletedBook.isPresent());
    }
}