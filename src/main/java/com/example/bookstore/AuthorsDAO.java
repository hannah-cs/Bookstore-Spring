package com.example.bookstore;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class AuthorsDAO {

    private DatabaseManager databaseManager;

    @Autowired
    public AuthorsDAO(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public List<Author> getAllAuthors() {
        List<Author> authors = new ArrayList<>();
        try {
            Connection connection = databaseManager.getConnection();
            System.out.println("Connected to the database successfully. Returning all authors.");
            String query = "SELECT * FROM authors";
            try (PreparedStatement pst = connection.prepareStatement(query);
                 ResultSet rs = pst.executeQuery()) {

                while (rs.next()) {
                    Author author = new Author(
                            rs.getInt("author_id"),
                            rs.getString("author_name")
                    );
                    authors.add(author);
                }
                databaseManager.closeConnection(connection);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            System.out.println("Error connecting to the database");
            e.printStackTrace();
        }
        return authors;
    }

    public List<Author> getAuthorById(int id) {
        List<Author> authors = new ArrayList<>();
        Connection connection = null;
        try {
            connection = databaseManager.getConnection();
            System.out.println("Connected to the database successfully. Returning authors by ID.");
            String query = "SELECT * FROM authors WHERE author_id = ?";
            try (PreparedStatement pst = connection.prepareStatement(query)) {
                pst.setInt(1, id);
                try (ResultSet rs = pst.executeQuery()) {
                    while (rs.next()) {
                        Author author = new Author(
                                rs.getInt("author_id"),
                                rs.getString("author_name")
                        );
                        authors.add(author);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            System.out.println("Error connecting to the database");
            e.printStackTrace();
        } finally {
            if (connection != null) {
                databaseManager.closeConnection(connection);
            }
        }
        return authors;
    }

    public List<Author> getAuthorByName(String name) {
        List<Author> authors = new ArrayList<>();
        try (Connection connection = databaseManager.getConnection()) {
            System.out.println("Connected to the database successfully. Returning authors by name.");
            String query = "SELECT * FROM authors WHERE author_name LIKE ?";
            try (PreparedStatement pst = connection.prepareStatement(query)) {
                pst.setString(1, "%" + name + "%");
                System.out.println("Connected to the database successfully. Searching author name column for " + name);
                try (ResultSet rs = pst.executeQuery()) {
                    while (rs.next()) {
                        Author author = new Author(
                                rs.getInt("author_id"),
                                rs.getString("author_name")
                        );
                        authors.add(author);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            System.out.println("Error connecting to the database");
            e.printStackTrace();
        }
        return authors;
    }

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

    public boolean createAuthor(Author newAuthor) {
        Connection connection = null;
        try {
            connection = databaseManager.getConnection();
            connection.setAutoCommit(false);

            String bookQuery = "INSERT INTO authors (author_name) VALUES (?)";
            try (PreparedStatement bookPst = connection.prepareStatement(bookQuery)) {
                bookPst.setString(1, newAuthor.getAuthor_name());
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

    public boolean updateAuthor(Author updatedAuthor) {
        try {
            Connection connection = databaseManager.getConnection();
            System.out.println("Connected to the database successfully. Updating author.");
            if (authorExists(updatedAuthor.getAuthor_id(), connection)) {
                String updateQuery = "UPDATE authors SET author_name = ? WHERE author_id = ?";
                try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                    updateStatement.setString(1, updatedAuthor.getAuthor_name());
                    updateStatement.setInt(2, updatedAuthor.getAuthor_id());
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

    private boolean authorExists(int id, Connection connection) throws SQLException {
        String checkQuery = "SELECT COUNT(*) FROM authors WHERE author_id=?";
        try (PreparedStatement checkStatement = connection.prepareStatement(checkQuery)) {
            checkStatement.setInt(1, id);
            ResultSet resultSet = checkStatement.executeQuery();
            resultSet.next();
            int count = resultSet.getInt(1);
            return count > 0;
        }
    }

    public boolean deleteAuthor(int id) {
        try {
            Connection connection = databaseManager.getConnection();
            System.out.println("Connected to the database successfully. Deleting author "+id);
            if (authorExists(id, connection)) {
                String updateQuery = "DELETE FROM authors WHERE author_id = ?";
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
}
