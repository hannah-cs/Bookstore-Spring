package com.example.bookstore;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/books")
public class BooksController {

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Book>> getBooks() {
        List<Book> books = new ArrayList<>();

        try {
            Connection connection = DatabaseManager.getConnection();
            System.out.println("Connected to the database successfully. All books:");

            String query = "SELECT * FROM books;";
            try (PreparedStatement pst = connection.prepareStatement(query);
                 ResultSet rs = pst.executeQuery()) {

                while (rs.next()) {
                    Book book = new Book(
                            rs.getInt("id"),
                            rs.getString("title"),
                            rs.getString("author"),
                            rs.getInt("price"),
                            rs.getInt("quantity")
                    );
                    books.add(book);
                }
                DatabaseManager.closeConnection(connection);

                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(books);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            System.out.println("Error connecting to the database");
            e.printStackTrace();
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }


    @GetMapping(value = "/id/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Book>> getBookById(@PathVariable int id) {
        List<Book> books = new ArrayList<>();
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement pst = connection.prepareStatement("SELECT * FROM books WHERE id = ?")) {
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Book book = new Book(
                            rs.getInt("id"),
                            rs.getString("title"),
                            rs.getString("author"),
                            rs.getInt("price"),
                            rs.getInt("quantity")
                    );
                    books.add(book);
                }
                DatabaseManager.closeConnection(connection);
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(books);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            System.out.println("Error connecting to the database");
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @GetMapping(value = "/title/{title}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Book>> getBooksByTitle(@PathVariable String title) {
        List<Book> books = new ArrayList<>();
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement pst = connection.prepareStatement("SELECT * FROM books WHERE title LIKE ?")) {
            pst.setString(1, "%" + title + "%");
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Book book = new Book(
                            rs.getInt("id"),
                            rs.getString("title"),
                            rs.getString("author"),
                            rs.getInt("price"),
                            rs.getInt("quantity")
                    );
                    books.add(book);
                }
                DatabaseManager.closeConnection(connection);
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(books);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            System.out.println("Error connecting to the database");
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @GetMapping(value = "/author/{author}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Book>> getBooksByAuthor(@PathVariable String author) {
        List<Book> books = new ArrayList<>();
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement pst = connection.prepareStatement("SELECT * FROM books WHERE author LIKE ?")) {
            pst.setString(1, "%" + author + "%");
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Book book = new Book(
                            rs.getInt("id"),
                            rs.getString("title"),
                            rs.getString("author"),
                            rs.getInt("price"),
                            rs.getInt("quantity")
                    );
                    books.add(book);
                }
                DatabaseManager.closeConnection(connection);
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(books);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            System.out.println("Error connecting to the database");
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createBook(@RequestBody Book newBook) {
        try {
            Connection connection = DatabaseManager.getConnection();
            String query = "INSERT INTO books (title, author, price, quantity) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pst = connection.prepareStatement(query)) {
                pst.setString(1, newBook.getTitle());
                pst.setString(2, newBook.getAuthor());
                pst.setDouble(3, newBook.getPrice());
                pst.setInt(4, newBook.getQuantity());
                int rowsAffected = pst.executeUpdate();
                DatabaseManager.closeConnection(connection);
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).build();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            System.out.println("Error connecting to the database");
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateBook(@RequestBody Book updatedBook) {
        try {
            Connection connection = DatabaseManager.getConnection();

            if (bookExists(updatedBook.getId(), connection)) {
                String updateQuery = "UPDATE books SET title=?, author=?, price=?, quantity=? WHERE id=?";
                try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                    updateStatement.setString(1, updatedBook.getTitle());
                    updateStatement.setString(2, updatedBook.getAuthor());
                    updateStatement.setDouble(3, updatedBook.getPrice());
                    updateStatement.setInt(4, updatedBook.getQuantity());
                    updateStatement.setInt(5, updatedBook.getId());
                    int rowsAffected = updateStatement.executeUpdate();
                    DatabaseManager.closeConnection(connection);

                    return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).build();

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                DatabaseManager.closeConnection(connection);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (SQLException e) {
            System.out.println("Error connecting to the database");
            e.printStackTrace();
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    private boolean bookExists(int bookId, Connection connection) throws SQLException {
        String checkQuery = "SELECT COUNT(*) FROM books WHERE id=?";
        try (PreparedStatement checkStatement = connection.prepareStatement(checkQuery)) {
            checkStatement.setInt(1, bookId);
            ResultSet resultSet = checkStatement.executeQuery();
            resultSet.next();
            int count = resultSet.getInt(1);
            return count > 0;
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable int id) {
        try {
            Connection connection = DatabaseManager.getConnection();

            if (bookExists(id, connection)) {
                String updateQuery = "DELETE FROM books WHERE id = ?";
                try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                    updateStatement.setInt(1, id);
                    int rowsAffected = updateStatement.executeUpdate();
                    DatabaseManager.closeConnection(connection);

                    return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).build();

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                DatabaseManager.closeConnection(connection);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (SQLException e) {
            System.out.println("Error connecting to the database");
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}