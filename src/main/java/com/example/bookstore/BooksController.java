package com.example.bookstore;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/books")
public class BooksController {

    private BooksDAO booksDAO;

    @Autowired
    public BooksController(BooksDAO booksDAO) {
        this.booksDAO = booksDAO;
    }

    @PostConstruct
    public void init() {
        System.out.println("BooksController bean initialized");
    }

    @PreDestroy
    public void cleanUp() {

        System.out.println("BooksController bean destroyed");
    }

    // endpoints


    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Book>> getBooks() {
        List<Book> books = booksDAO.getAllBooks();
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(books);
    }
    @GetMapping(value = "/id/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Book>> getBookById(@PathVariable int id) {
        List<Book> books = booksDAO.getBookById(id);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(books);
    }

    @GetMapping(value = "/title/{title}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Book>> getBooksByTitle(@PathVariable String title) {
        List<Book> books = booksDAO.getBooksByTitle(title);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(books);
    }

    @GetMapping(value = "/author/{author}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Book>> getBooksByAuthor(@PathVariable String author) {
        List<Book> books = booksDAO.getBooksByAuthor(author);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(books);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createBook(@RequestBody Book newBook) {
        boolean success = booksDAO.createBook(newBook);
        if (success) {
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).build();
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateBook(@RequestBody Book updatedBook) {
        boolean success = booksDAO.updateBook(updatedBook);
        if (success) {
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable int id) {
        boolean success = booksDAO.deleteBook(id);
        if (success) {
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

}