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
@RequestMapping("/authors")
public class AuthorsController {
    private AuthorsDAO authorsDAO;

    @Autowired
    public AuthorsController(AuthorsDAO authorsDAO) {
        this.authorsDAO = authorsDAO;
    }

    @PostConstruct
    public void init() {
        System.out.println("AuthorsController bean initialized");
    }

    @PreDestroy
    public void cleanUp() {
        System.out.println("AuthorsController bean destroyed");
    }

    // endpoints

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Author>> getAuthors() {
        List<Author> authors = authorsDAO.getAllAuthors();
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(authors);
    }
    @GetMapping(value = "/id/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Author>> getAuthorById(@PathVariable int id) {
        List<Author> authors = authorsDAO.getAuthorById(id);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(authors);
    }

    @GetMapping(value = "/name/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Author>> getAuthorByName(@PathVariable String name) {
        List<Author> authors = authorsDAO.getAuthorByName(name);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(authors);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createAuthor(@RequestBody Author newAuthor) {
        boolean success = authorsDAO.createAuthor(newAuthor);
        if (success) {
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).build();
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateAuthor(@RequestBody Author updatedAuthor) {
        boolean success = authorsDAO.updateAuthor(updatedAuthor);
        if (success) {
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteAuthor(@PathVariable int id) {
        boolean success = authorsDAO.deleteAuthor(id);
        if (success) {
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
