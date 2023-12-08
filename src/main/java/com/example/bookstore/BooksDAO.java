package com.example.bookstore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
    public class BooksDAO {

        private DatabaseManager databaseManager;

        @Autowired
        public BooksDAO(DatabaseManager databaseManager) {
            this.databaseManager = databaseManager;
        }

    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        try {
            Connection connection = databaseManager.getConnection();
            System.out.println("Connected to the database successfully. Returning all books.");
            String query = "SELECT b.id, b.title, a.author_name AS author, b.price, b.quantity " +
                    "FROM books b " +
                    "JOIN authors a ON b.author_id = a.author_id;";
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
                databaseManager.closeConnection(connection);
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
            try (Connection connection = databaseManager.getConnection();
                 PreparedStatement pst = connection.prepareStatement("SELECT b.id, b.title, a.author_name AS author, b.price, b.quantity FROM books b JOIN authors a ON b.author_id = a.author_id WHERE b.id = ?")) {
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
                    databaseManager.closeConnection(connection);
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
            try (Connection connection = databaseManager.getConnection();
                 PreparedStatement pst = connection.prepareStatement("SELECT b.id, b.title, a.author_name AS author, b.price, b.quantity FROM books b JOIN authors a ON b.author_id = a.author_id WHERE title LIKE ?")) {
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
                    databaseManager.closeConnection(connection);
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
            try (Connection connection = databaseManager.getConnection();
                 PreparedStatement pst = connection.prepareStatement("SELECT b.id, b.title, a.author_name AS author, b.price, b.quantity FROM books b JOIN authors a ON b.author_id = a.author_id WHERE author_name LIKE ?")) {
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
                    databaseManager.closeConnection(connection);

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
        Connection connection = null;
        try {
            connection = databaseManager.getConnection();
            connection.setAutoCommit(false);
            int authorId = getAuthorId(connection, newBook.getAuthor());

            if (authorId == -1) {
                authorId = insertAuthor(connection, newBook.getAuthor());
            }

            String bookQuery = "INSERT INTO books (title, author_id, price, quantity) VALUES (?, ?, ?, ?)";
            try (PreparedStatement bookPst = connection.prepareStatement(bookQuery)) {
                bookPst.setString(1, newBook.getTitle());
                bookPst.setInt(2, authorId);
                bookPst.setDouble(3, newBook.getPrice());
                bookPst.setInt(4, newBook.getQuantity());
                int rowsAffectedBooks = bookPst.executeUpdate();
                if (rowsAffectedBooks > 0) {
                    connection.commit();
                } else {
                    connection.rollback();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    databaseManager.closeConnection(connection);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }


    public boolean updateBook(Book updatedBook) {
            try {
                Connection connection = databaseManager.getConnection();
                System.out.println("Connected to the database successfully. Updating book.");
                int authorId = getAuthorId(connection, updatedBook.getAuthor());
                if (bookExists(updatedBook.getId(), connection)) {
                    String updateQuery = "UPDATE books SET title=?, author_id=?, price=?, quantity=? WHERE id=?";
                    try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                        updateStatement.setString(1, updatedBook.getTitle());
                        updateStatement.setInt(2, authorId);
                        updateStatement.setDouble(3, updatedBook.getPrice());
                        updateStatement.setInt(4, updatedBook.getQuantity());
                        updateStatement.setInt(5, updatedBook.getId());
                        int rowsAffected = updateStatement.executeUpdate();
                        databaseManager.closeConnection(connection);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } else {
                    databaseManager.closeConnection(connection);
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
                Connection connection = databaseManager.getConnection();
                System.out.println("Connected to the database successfully. Deleting book "+id);
                if (bookExists(id, connection)) {
                    String updateQuery = "DELETE FROM books WHERE id = ?";
                    try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                        updateStatement.setInt(1, id);
                        int rowsAffected = updateStatement.executeUpdate();
                        databaseManager.closeConnection(connection);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } else {
                    databaseManager.closeConnection(connection);
                }
            } catch (SQLException e) {
                System.out.println("Error connecting to the database");
                e.printStackTrace();
            }
            return true;
        }

        // methods to avoid duplicate authors

    private int getAuthorId(Connection connection, String authorName) {
        String query = "SELECT author_id FROM authors WHERE author_name = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setString(1, authorName);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("author_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private int insertAuthor(Connection connection, String authorName) {
        String insertQuery = "INSERT INTO authors (author_name) VALUES (?)";
        try (PreparedStatement insertPst = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
            insertPst.setString(1, authorName);
            int rowsAffectedAuthors = insertPst.executeUpdate();

            if (rowsAffectedAuthors > 0) {
                try (ResultSet generatedKeys = insertPst.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    }