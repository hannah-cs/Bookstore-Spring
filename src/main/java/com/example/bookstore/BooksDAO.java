package com.example.bookstore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;

    @Repository
    public class BooksDAO {

        public List<Book> getAllBooks() {
            List<Book> books = new ArrayList<>();
            try {
                Connection connection = DatabaseManager.getConnection();
                System.out.println("Connected to the database successfully. Returning all books.");
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
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } catch (SQLException e) {
                System.out.println("Error connecting to the database");
                e.printStackTrace();
            }
            return books;
        }

        public List<Book> getBookById(int id) {
            List<Book> books = new ArrayList<>();
            try (Connection connection = DatabaseManager.getConnection();
                 PreparedStatement pst = connection.prepareStatement("SELECT * FROM books WHERE id = ?")) {
                pst.setInt(1, id);
                System.out.println("Connected to the database successfully. Searching ID column for "+id);
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
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } catch (SQLException e) {
                System.out.println("Error connecting to the database");
                e.printStackTrace();
            }
            return books;
        }

        public List<Book> getBooksByTitle(String title) {
            List<Book> books = new ArrayList<>();
            try (Connection connection = DatabaseManager.getConnection();
                 PreparedStatement pst = connection.prepareStatement("SELECT * FROM books WHERE title LIKE ?")) {
                pst.setString(1, "%" + title + "%");
                System.out.println("Connected to the database successfully. Searching title column for "+title);
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
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } catch (SQLException e) {
                System.out.println("Error connecting to the database");
                e.printStackTrace();
            }
            return books;
        }

        public List<Book> getBooksByAuthor(String author) {
            List<Book> books = new ArrayList<>();
            try (Connection connection = DatabaseManager.getConnection();
                 PreparedStatement pst = connection.prepareStatement("SELECT * FROM books WHERE author LIKE ?")) {
                pst.setString(1, "%" + author + "%");
                System.out.println("Connected to the database successfully. Searching author column for "+author);
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

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } catch (SQLException e) {
                System.out.println("Error connecting to the database");
                e.printStackTrace();
            }
            return books;
        }

        public boolean createBook(Book newBook) {
            try {
                Connection connection = DatabaseManager.getConnection();
                String query = "INSERT INTO books (title, author, price, quantity) VALUES (?, ?, ?, ?)";
                System.out.println("Connected to the database successfully. Creating new book.");
                try (PreparedStatement pst = connection.prepareStatement(query)) {
                    pst.setString(1, newBook.getTitle());
                    pst.setString(2, newBook.getAuthor());
                    pst.setDouble(3, newBook.getPrice());
                    pst.setInt(4, newBook.getQuantity());
                    int rowsAffected = pst.executeUpdate();
                    DatabaseManager.closeConnection(connection);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } catch (SQLException e) {
                System.out.println("Error connecting to the database");
                e.printStackTrace();
            }
            return true;
        }

        public boolean updateBook(Book updatedBook) {
            try {
                Connection connection = DatabaseManager.getConnection();
                System.out.println("Connected to the database successfully. Updating book.");
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
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } else {
                    DatabaseManager.closeConnection(connection);
                }
            } catch (SQLException e) {
                System.out.println("Error connecting to the database");
                e.printStackTrace();
            }
            return true;
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

        public boolean deleteBook(int id) {
            try {
                Connection connection = DatabaseManager.getConnection();
                System.out.println("Connected to the database successfully. Deleting book "+id);
                if (bookExists(id, connection)) {
                    String updateQuery = "DELETE FROM books WHERE id = ?";
                    try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                        updateStatement.setInt(1, id);
                        int rowsAffected = updateStatement.executeUpdate();
                        DatabaseManager.closeConnection(connection);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } else {
                    DatabaseManager.closeConnection(connection);
                }
            } catch (SQLException e) {
                System.out.println("Error connecting to the database");
                e.printStackTrace();
            }
            return true;
        }
    }